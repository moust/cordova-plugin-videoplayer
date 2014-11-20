Video Player plugin for Cordova/PhoneGap
========================================

A Codova plugin that simply allows you to immediately play a video in fullscreen mode.


# Installation

This plugin use the Cordova CLI's plugin command. To install it to your application, simply execute the following (and replace variables).

```
cordova plugin add com.moust.cordova.videoplayer
```


# Using

Just call de `play` method with a video file path as argument.

```
VideoPlayer.get(path, [options], [completeCallback], [errorCallback]);
```

The plugin is abble to play file-path or http/rtsp URL.

You can optionally add options parameters like volume and caling mode.
You can also add an success callback function to handle completed playback.
You can also add an error callback function to handle unexpected playback errors.

## Example

```javascript
VideoPlayer.play("file:///android_asset/www/movie.mp4");
```

```javascript
VideoPlayer.play(
    "file:///android_asset/www/movie.mp4",
    {
        volume: 0.5,
        scalingMode: VideoPlayer.SCALING_MODE.SCALE_TO_FIT_WITH_CROPPING
    },
    function () {
        console.log("video completed");
    }
    function (err) {
        console.log(err);
    }
);
```

## Options

- `volume`: (Optional) allows you to sets the volume on this player. Note that the passed volume value is raw scalars in range 0.0 to 1.0.

- `scalingMode`: (Optional) allows you to sets video scaling mode.

    The following constants are the only values availables for the `scalingMode` option:

    - `VIDEO_SCALING_MODE_SCALE_TO_FIT` (default)
    - `VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING`

    Refer to http://developer.android.com/reference/android/media/MediaPlayer.html#setVideoScalingMode(int) for more details.


# Licence MIT

Copyright 2013 Quentin Aupetit

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
