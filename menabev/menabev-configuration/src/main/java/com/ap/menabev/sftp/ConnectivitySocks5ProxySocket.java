package com.ap.menabev.sftp;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Base64; // or any other library for base64 encoding

import org.json.JSONArray; // or any other library for JSON objects
import org.json.JSONException; // or any other library for JSON objects
import org.json.JSONObject; // or any other library for JSON objects

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectivitySocks5ProxySocket extends Socket {

    private static final byte SOCKS5_VERSION = 0x05;
    private static final byte SOCKS5_JWT_AUTHENTICATION_METHOD = (byte) 0x80;
    private static final byte SOCKS5_JWT_AUTHENTICATION_METHOD_VERSION = 0x01;
    private static final byte SOCKS5_COMMAND_CONNECT_BYTE = 0x01;
    private static final byte SOCKS5_COMMAND_REQUEST_RESERVED_BYTE = 0x00;
    private static final byte SOCKS5_COMMAND_ADDRESS_TYPE_IPv4_BYTE = 0x01;
    private static final byte SOCKS5_COMMAND_ADDRESS_TYPE_DOMAIN_BYTE = 0x03;
    private static final byte SOCKS5_AUTHENTICATION_METHODS_COUNT = 0x01;
    private static final int SOCKS5_JWT_AUTHENTICATION_METHOD_UNSIGNED_VALUE = 0x80 & 0xFF;
    private static final byte SOCKS5_AUTHENTICATION_SUCCESS_BYTE = 0x00;

    private static final String SOCKS5_PROXY_HOST_PROPERTY = "onpremise_proxy_host";
    private static final String SOCKS5_PROXY_PORT_PROPERTY = "onpremise_socks5_proxy_port";

    private final String jwtToken;
    private final String sccLocationId;

    @Override
    public void connect(SocketAddress endpoint) throws IOException {
        super.connect(endpoint);
    }

    public ConnectivitySocks5ProxySocket(String sccLocationId) throws JSONException {
        this.jwtToken = getJwtToken();
        this.sccLocationId = sccLocationId != null ? Base64.getEncoder().encodeToString(sccLocationId.getBytes()) : "";
    }

    private String getJwtToken() throws JSONException{
        JSONObject credentials = extractEnvironmentCredentials();
        String clientId = credentials.getString("clientid");
        String clientSecret = credentials.getString("clientsecret");
        String tokenUrl = credentials.getString("token_service_url");

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(tokenUrl + "/oauth/token?grant_type=client_credentials")
                .method("POST", body)
                .addHeader("clientid", clientId)
                .addHeader("clientsecret", clientSecret)
                .addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()))
                .build();

        try {
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            System.err.println(res);
            JSONObject o = new JSONObject(res);
            return o.getString("access_token");
        } catch (IOException e) {
            System.err.println(e);
        }

        return "";
    }

    protected InetSocketAddress getProxyAddress() {
        try {
            JSONObject credentials = extractEnvironmentCredentials();
            String proxyHost = credentials.getString(SOCKS5_PROXY_HOST_PROPERTY);
            int proxyPort = Integer.parseInt(credentials.getString(SOCKS5_PROXY_PORT_PROPERTY));
            return new InetSocketAddress(proxyHost, proxyPort);
        } catch (JSONException ex) {
            throw new IllegalStateException("Unable to extract the SOCKS5 proxy host and port", ex);
        }
    }

    private JSONObject extractEnvironmentCredentials() throws JSONException {
        JSONObject jsonObj = new JSONObject(System.getenv("VCAP_SERVICES"));
        JSONArray jsonArr = jsonObj.getJSONArray("connectivity");
        return jsonArr.getJSONObject(0).getJSONObject("credentials");
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        super.connect(getProxyAddress(), timeout);

        OutputStream outputStream = getOutputStream();

        executeSOCKS5InitialRequest(outputStream);

        executeSOCKS5AuthenticationRequest(outputStream);

        executeSOCKS5ConnectRequest(outputStream, (InetSocketAddress) endpoint);
    }

    private void executeSOCKS5InitialRequest(OutputStream outputStream) throws IOException {
        byte[] initialRequest = createInitialSOCKS5Request();
        outputStream.write(initialRequest);

        assertServerInitialResponse();
    }

    private byte[] createInitialSOCKS5Request() throws IOException {
        ByteArrayOutputStream byteArraysStream = new ByteArrayOutputStream();
        try {
            byteArraysStream.write(SOCKS5_VERSION);
            byteArraysStream.write(SOCKS5_AUTHENTICATION_METHODS_COUNT);
            byteArraysStream.write(SOCKS5_JWT_AUTHENTICATION_METHOD);
            return byteArraysStream.toByteArray();
        } finally {
            byteArraysStream.close();
        }
    }

    private void assertServerInitialResponse() throws IOException {
        InputStream inputStream = getInputStream();

        int versionByte = inputStream.read();
        if (SOCKS5_VERSION != versionByte) {
            throw new SocketException(String.format("Unsupported SOCKS version - expected %s, but received %s", SOCKS5_VERSION, versionByte));
        }

        int authenticationMethodValue = inputStream.read();
        if (SOCKS5_JWT_AUTHENTICATION_METHOD_UNSIGNED_VALUE != authenticationMethodValue) {
            throw new SocketException(String.format("Unsupported authentication method value - expected %s, but received %s",
                    SOCKS5_JWT_AUTHENTICATION_METHOD_UNSIGNED_VALUE, authenticationMethodValue));
        }
    }

    private void executeSOCKS5AuthenticationRequest(OutputStream outputStream) throws IOException {
        byte[] authenticationRequest = createJWTAuthenticationRequest();
        outputStream.write(authenticationRequest);

        assertAuthenticationResponse();
    }

    private byte[] createJWTAuthenticationRequest() throws IOException {
        ByteArrayOutputStream byteArraysStream = new ByteArrayOutputStream();
        try {
            byteArraysStream.write(SOCKS5_JWT_AUTHENTICATION_METHOD_VERSION);
            byteArraysStream.write(ByteBuffer.allocate(4).putInt(jwtToken.getBytes().length).array());
            byteArraysStream.write(jwtToken.getBytes());
            byteArraysStream.write(ByteBuffer.allocate(1).put((byte) sccLocationId.getBytes().length).array());
            byteArraysStream.write(sccLocationId.getBytes());
            return byteArraysStream.toByteArray();
        } finally {
            byteArraysStream.close();
        }
    }

    private void assertAuthenticationResponse() throws IOException {
        InputStream inputStream = getInputStream();

        int authenticationMethodVersion = inputStream.read();
        if (SOCKS5_JWT_AUTHENTICATION_METHOD_VERSION != authenticationMethodVersion) {
            throw new SocketException(String.format("Unsupported authentication method version - expected %s, but received %s",
                    SOCKS5_JWT_AUTHENTICATION_METHOD_VERSION, authenticationMethodVersion));
        }

        int authenticationStatus = inputStream.read();
        if (SOCKS5_AUTHENTICATION_SUCCESS_BYTE != authenticationStatus) {
            throw new SocketException("Authentication failed!");
        }
    }

    private void executeSOCKS5ConnectRequest(OutputStream outputStream, InetSocketAddress endpoint) throws IOException {
        byte[] commandRequest = createConnectCommandRequest(endpoint);
        outputStream.write(commandRequest);

        assertConnectCommandResponse();
    }

    private byte[] createConnectCommandRequest(InetSocketAddress endpoint) throws IOException {
        String host = endpoint.getHostName();
        int port = endpoint.getPort();
        ByteArrayOutputStream byteArraysStream = new ByteArrayOutputStream();
        try {
            byteArraysStream.write(SOCKS5_VERSION);
            byteArraysStream.write(SOCKS5_COMMAND_CONNECT_BYTE);
            byteArraysStream.write(SOCKS5_COMMAND_REQUEST_RESERVED_BYTE);
            byte[] hostToIPv4 = parseHostToIPv4(host);
            if (hostToIPv4 != null) {
                byteArraysStream.write(SOCKS5_COMMAND_ADDRESS_TYPE_IPv4_BYTE);
                byteArraysStream.write(hostToIPv4);
            } else {
                byteArraysStream.write(SOCKS5_COMMAND_ADDRESS_TYPE_DOMAIN_BYTE);
                byteArraysStream.write(ByteBuffer.allocate(1).put((byte) host.getBytes().length).array());
                byteArraysStream.write(host.getBytes());
            }
            byteArraysStream.write(ByteBuffer.allocate(2).putShort((short) port).array());
            return byteArraysStream.toByteArray();
        } finally {
            byteArraysStream.close();
        }
    }

    private void assertConnectCommandResponse() throws IOException {
        InputStream inputStream = getInputStream();

        int versionByte = inputStream.read();
        if (SOCKS5_VERSION != versionByte) {
            throw new SocketException(String.format("Unsupported SOCKS version - expected %s, but received %s", SOCKS5_VERSION, versionByte));
        }

        int connectStatusByte = inputStream.read();
        assertConnectStatus(connectStatusByte);

        readRemainingCommandResponseBytes(inputStream);
    }

    private void assertConnectStatus(int commandConnectStatus) throws IOException {
        if (commandConnectStatus == 0) {
            return;
        }

        String commandConnectStatusTranslation;
        switch (commandConnectStatus) {
            case 1:
                commandConnectStatusTranslation = "FAILURE";
                break;
            case 2:
                commandConnectStatusTranslation = "FORBIDDEN";
                break;
            case 3:
                commandConnectStatusTranslation = "NETWORK_UNREACHABLE";
                break;
            case 4:
                commandConnectStatusTranslation = "HOST_UNREACHABLE";
                break;
            case 5:
                commandConnectStatusTranslation = "CONNECTION_REFUSED";
                break;
            case 6:
                commandConnectStatusTranslation = "TTL_EXPIRED";
                break;
            case 7:
                commandConnectStatusTranslation = "COMMAND_UNSUPPORTED";
                break;
            case 8:
                commandConnectStatusTranslation = "ADDRESS_UNSUPPORTED";
                break;
            default:
                commandConnectStatusTranslation = "UNKNOWN";
                break;
        }
        throw new SocketException("SOCKS5 command failed with status: " + commandConnectStatusTranslation);
    }

    private byte[] parseHostToIPv4(String hostName) {
        byte[] parsedHostName = null;
        String[] virtualHostOctets = hostName.split("\\.", -1);
        int octetsCount = virtualHostOctets.length;
        if (octetsCount == 4) {
            try {
                byte[] ipOctets = new byte[octetsCount];
                for (int i = 0; i < octetsCount; i++) {
                    int currentOctet = Integer.parseInt(virtualHostOctets[i]);
                    if ((currentOctet < 0) || (currentOctet > 255)) {
                        throw new IllegalArgumentException(String.format("Provided octet %s is not in the range of [0-255]", currentOctet));
                    }
                    ipOctets[i] = (byte) currentOctet;
                }
                parsedHostName = ipOctets;
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }

        return parsedHostName;
    }

    private void readRemainingCommandResponseBytes(InputStream inputStream) throws IOException {
        inputStream.read(); // skipping over SOCKS5 reserved byte
        int addressTypeByte = inputStream.read();
        if (SOCKS5_COMMAND_ADDRESS_TYPE_IPv4_BYTE == addressTypeByte) {
            for (int i = 0; i < 6; i++) {
                inputStream.read();
            }
        } else if (SOCKS5_COMMAND_ADDRESS_TYPE_DOMAIN_BYTE == addressTypeByte) {
            int domainNameLength = inputStream.read();
            int portBytes = 2;
            inputStream.read(new byte[domainNameLength + portBytes], 0, domainNameLength + portBytes);
        }
    }
}
