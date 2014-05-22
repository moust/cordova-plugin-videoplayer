var exec = require("cordova/exec");

module.exports = {

    play: function(path, errorCallback) {
        exec(null, errorCallback, "VideoPlayer", "play", [path]);
    }

};