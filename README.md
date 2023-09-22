# BurpMontoyaAccessTokenHelper

This plugin was written for an application that automatically refreshes the access token while the browser sits idle. I wanted to always have the most up to date access token appended to requests for scanner and repeater without having to do it manually. Therefore, I wrote this plugin such that it:

- Monitors proxy responses for new AccessTokens from a particular URL
- Stores the most recent access token
- Provides a session macro that you can apply to update the current access token for requests to a particular URL prefix

## How to Build It
### Setup Github Token to Access Packages

1. Log into your personal github account and create an access token that can "Read Packages"
2. `export GHUSERNAME="yourusernamehere"`
3. `export GHTOKEN="youraccestokenhere"`

### Customize
1. Review the URLs hardcoded into the plugin and adjust to fit the application you are testing
2. Review the way the access token is updated in the request and make sure it matches your application

### Build
#### Via Command-Line
```bash
$ ./gradlew fatJar
```
#### Via InteliJ
1. Open the project in Intellij
2. Open the Gradle sidebar on the right hand side
3. Choose Tasks -> Other -> fatJar

## How to add this plugin to Burp
1. Open Burp Suite
2. Go to Extensions -> Installed -> Add
    - Extension Type: Java
    - Extension file: build/libs/burpmontoyaccesstokenhelper-0.1-fatjar.jar