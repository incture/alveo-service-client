
var mqclient = require('mqclient/amqp')

// TODO: need a client signature to be able to trigger refresh requests
module.exports = function (clientSignature) {

  return {

    // used for sending refresh requests
    refresh: function (collectorName, params, callback) {

      var refreshRequestChannel = 'collector:' + collectorName + ':refresh:'
      //console.log('REFRESH: ', refreshRequestChannel, params)
      mqclient.pub(refreshRequestChannel, params, function () {
        //console.log('REQUESTED')
        return callback && callback()
      })

    },

    // abstracted interface for listening to MQ messages
    listen: function (channels, callback) {

      //console.log('LISTENING: ', channel)

      mqclient.sub(channel, function (err, msg) {

        var result

        try {
          result = JSON.parse(msg)
        }
        catch (err) {
          result = msg
        }

        callback(err, result)

      })

    }

  }

}
