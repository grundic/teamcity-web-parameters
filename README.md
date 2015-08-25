Teamcity web parameters
=======================
Web-parameters is plugin for JetBrains Teamcity server, that extends default dropdown box with parameters populated from web service.
With this plugin you can make your builds more flexible. For example, you can populate options from
  - database query
  - VCS listing (folder, tag)
  - LDAP
  - name your own

![demo](https://github.com/grundic/teamcity-web-parameters/blob/master/demo/teamcity-web-parameters.gif?raw=true)

Installation
-------------
To install plugin [download zip archive](https://bintray.com/grundic/generic/teamcity-web-parameters/view/files) it and copy it to Teamcity <data directory>/plugins. For more information, take a look at [official documentation](https://confluence.jetbrains.com/display/TCD8/Installing+Additional+Plugins)

Web-service
-----------
In order to correct plugin work, you have to have working web-service and Teamcity must have access to it. The web-service could be anything, that produces json or xml response in specific format.
For testing purposes, there is `webserver` directory, with sample files. Here is an example of json format:
```json
{
    "options": [
        {
            "key": "first",
            "value": "first",
            "enabled": true
        },
        {
            "key": "second",
            "value": "second",
            "enabled": true
        },
        {
            "key": "third",
            "value": "third",
            "enabled": true
        },
        {
            "key": "fourth",
            "value": "4444",
            "enabled": true
        }
    ]
}
```
As you can see, format is simple and straightforward. Each element in `options`'s array is hash; `key`  will be option's display name, `value` will be option's value, `enabled` will determine if option will be enabled.
To run your web-service, navigate to `webserver` directory and execute a command:
```python -m SimpleHTTPServer 8099```
It will start simple http server on port 8099, which will serve your local directory, where command was executed. This is not a production web service and should be used in testing environment only.

Configuration
-------------
After plugin is installed and web-service is functioning, you can start using it.
Open build configuration and navigate to `Parameters` settings. Click `Add new parameter`; `Name`, `Kind` and `Value` configure as usual. Next, click on `Edit` button from `Spec` block. 
Select `Web populated select` in `Type` option (if you don't see such an option, make sure you have installed plugin correctly and Teamcity loaded if without errors).
In `URL` option enter url of your web service. For our aforementioned python example it should be ```http://localhost:8099/options.json```.
`Response format` should be selected either `xml` if your web service produces xml data or `json` in case of json. For testing configuration select json.

License
-------
[MIT](https://github.com/grundic/teamcity-web-parameters/blob/master/LICENSE)

