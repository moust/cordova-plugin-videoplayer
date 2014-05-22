var exec = require("cordova/exec");

module.exports = {

    DEFAULT_OPTIONS: {
        volume: 1.0,
        scalingMode: 1
    },

    SCALING_MODE: {
        SCALE_TO_FIT: 1,
        SCALE_TO_FIT_WITH_CROPPING: 2
    },

    merge: function(obj) {
        if (false === (obj === Object(obj))) return obj;
        Array.prototype.slice.call(arguments, 1).forEach(function(source) {
            for (var prop in source) {
                obj[prop] = source[prop];
            }
        });
        return obj;
    },

    play: function(path, options, errorCallback) {
        options = this.merge(this.DEFAULT_OPTIONS, options);
        exec(null, errorCallback, "VideoPlayer", "play", [path, options]);
    }

};