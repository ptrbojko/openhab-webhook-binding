# Webhook Binding

This even lets you describe your own API/WebHooks for OpenHab. This is sometimes needed for external services or hardware that has limited ability to call http webhooks.

## Supported Things

Only one thing is supported - webhook. This thing does not have any channels at first but let you add any number of it.

## Discovery

Discovery is not supported

## Binding Configuration

Main configuration of the created thing lies in its unique id. This is will be part of the url for a webhook endpoint processed by this thing.

Optional config is to fill _Response expression_ / _Expression_ . However for most cases you can leave with 
```
resp.status=200
```

## Channel configuration

Within a channel you configure only an expression property. Configuration is in form of a script written in Apache JEXL language. Return value of this expression will be a value that updates/commands the channel.

As for now in the expression there is only a simple object req which has only two properties:

1. **parameters** - under this property all query parameters and form-encoded post parameters are placed. So you can define in your http requests query params and extract it to channels.
2. **body** - unders this property the body of the POST requests is stored. body has two properties text and json. With this you can post a json file to you openhab and use an expression to extract an value from the json and place it into channel. See further examples.
3. **method** - POST/GET/PUT/DELETE

Context for expressions can be enriched with more than one req object in future.

### Example

Following exaple of channel expression will update value of the channel whenever someone make a request to **http://[youropenhab]/webhook/[thingid]/[channelid]**.

```
if (req.parameters.action[0] == "call") {
  return "CALLING";
};
```

# Suppport this project

You can support this project by sponsoring its maintainers:
[Piotr Bojko](https://github.com/sponsors/ptrbojko?frequency=one-time)