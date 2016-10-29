Teamcity web parameters
=======================
Web-parameters is plugin for JetBrains Teamcity server, that extends default dropdown box with parameters populated from web service.
With this plugin you can make your builds more flexible, as your parameters would be populated on fly. For example, you can fetch options from
  - database query
  - VCS listing (folder, tag)
  - LDAP
  - name your own

Here are some highlights of a plugin:
  - dynamic values from external web-service
  - url can use values from other parameters
  - supports images
  - possibility to configure single/multi select
  - tag support (user can add his own value)

![demo](https://github.com/grundic/teamcity-web-parameters/blob/master/demo/process-select.gif?raw=true)

More images available [here](https://github.com/grundic/teamcity-web-parameters/wiki/Demo).

Installation
-------------
To install plugin [download zip archive](https://github.com/grundic/teamcity-web-parameters/releases/latest) it and copy it to Teamcity <data directory>/plugins. For more information, take a look at [official documentation](https://confluence.jetbrains.com/display/TCD8/Installing+Additional+Plugins)

Web-service
-----------
Plugin requires, that you have a working web-service and Teamcity must have access to it.
The web-service could be anything, that produces json or xml response in specific format.
For testing purposes, there is `webserver` directory, with sample files. Here is an example of json format:
```json
{
    "options": [
        {
            "key": "First item",
            "value": "100",
            "enabled": true
        },
        {
            "key": "Second item",
            "value": "200",
            "enabled": true,
            "image": "http://lipis.github.io/flag-icon-css/flags/4x3/de.svg"
        },
        {
            "key": "Third item",
            "value": "300",
            "enabled": false
        },
        {
            "key": "Fourth item",
            "value": "400",
            "enabled": 0
        },
        {
            "key": "Fifth item",
            "value": "500",
            "enabled": true,
            "default": true
        }

    ]
}
```
As you can see, format is simple and straightforward. Each element in `options`'s array is hash;
Here is available options of hash:
  * `key` -- this option will be used as display name in select box
  * `value` -- this option will be used as parameter value during build
  * `enable` -- this option determines whether the corresponding option enabled on not. Setting to `false` will prevent
  specific option from selection. By default it's `true`.
  * `default` -- this option is used for setting default value if none was selected. You can mark multiple options as
  default, but the first found will be used.
  * `image` -- with help of this option you can add small icon for current element. Both url and embedded images are supported.

To run your web-service, navigate to `webserver` directory and execute a command:
```python -m SimpleHTTPServer 8099```
It will start simple http server on port 8099, which will serve your local directory, where command was executed.
This is not a production web service and should be used in testing environment only.
Also there is `processes_web_server.py` script, which can be used to demonstrate dynamic options: it lists currently running
processes on a host machine and uses name/pid for key/value. Please note, that it uses `psutil` library, so you migth need to
install it: ```pip install psutil```.

Configuration
-------------
After plugin is installed and web-service is functioning, you can start using it.
Open build configuration and navigate to `Parameters` settings. Click `Add new parameter`; `Name`, `Kind` and `Value` configure as usual.
Next, click on `Edit` button from `Spec` block.
Select `Web populated select` in `Type` option (if you don't see such an option, make sure you have installed plugin correctly and Teamcity loaded if without errors).
In `URL` option enter url of your web service. For our aforementioned python example it should be ```http://localhost:8099/options.json```.
`Response format` should be selected either `xml` if your web service produces xml data or `json` in case of json. For testing configuration select json.
If you wish, you could select `Prompt` in Display option -- this would show dialog box with specific parameter (this is default configuration option of TeamCity).
After that you can run build as usual and use dynamic parameters, enjoyning seamless build configuration.

Configuration options description
---------------------------------
| Option name | Description |
| ----------- | ----------- |
| URL | Url for requesting options, e.g. `http://localhost:8090`. This parameter could also use other parameters for expanding it's value: `http://%HOST%:%PORT%`. |
| Request timeout | Timeout of a request. This is optional parameter, by default set to 1 minute (60,000 ms). |
| Request method: | Method (verb) of a request. Currently `GET` and `POST` are supported. |
| Payload | Payload data to send to the server. This is optional parameter and only available for `POST` requests. |
| Headers | List of headers to send to the server. Every header should be on a new line and separated with colon: `header-name: header-value`. |
| Basic Authorization | It's possible to configure basic authorisation for server with options. In this case, provide username and password to corresponding fields. Please note, that currently it's not possible to mask password in this window, so it would be visible on a screen. |
| Response format | Expected response format from the server. Could be one of `json` and `xml`. |
| Allow multiple | Flag for configuring select component: if checked, user could select multiple values from drop box, which would be separated by given separator. |
| Value separator | Separator to use for multiple options. But default `,` would be used. |
| Tag support | Flag for configuring select component: if checked, it would be possible to enter new values into the drop box. |
| Enable edit on error: | This flag controls whether it's possible to enter user value in cause of unpredictable error. |

License
-------
[MIT](https://github.com/grundic/teamcity-web-parameters/blob/master/LICENSE)

