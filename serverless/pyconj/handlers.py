import json


def ok(body):
    return {'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Credentials': True
            },
            'body': body}


def bad_echo(event, context):
    return ok(event)


def echo(event, context):
    return ok(json.dumps(event))
