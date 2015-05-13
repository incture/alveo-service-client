
var mqclient = require('mqclient/amqp')

function refreshCollector(collectorName, params, callback) {
  var refreshRequestChannel = 'collector:' + collectorName + ':refresh:'
  //console.log('REFRESH: ', refreshRequestChannel, params)
  mqclient.pub(refreshRequestChannel, params, function () {
    //console.log('REQUESTED')
    return callback && callback()
  })
}

// TODO: need a client signature to be able to trigger refresh requests
module.exports = function (clientSignature) {

  return {

    // used for sending refresh requests - can accept a single name or an array of names
    refresh: function (collectorNames, params, callback) {

      if (Array.isArray(collectorNames)) {
        collectorNames.forEach(function (collectorName) {
          refreshCollector(collectorName, params, callback)
        })
      }
      else {
        refreshCollector(collectorNames, params, callback)
      }

    },

    // abstracted interface for listening to MQ messages
    listen: function (channels, callback) {

      //console.log('LISTENING: ', channel)

      var collectors = channels.collectors || []
      var processors = channels.processors || []

      var concatenatedChannels = collectors.concat(processors)

      concatenatedChannels.forEach(function (channel) {

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

      })

    }


  }

}
