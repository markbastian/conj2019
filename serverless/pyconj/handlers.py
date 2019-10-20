import json


def ok(body):
    return {'statusCode': 200,
            'headers': {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Credentials': True
            },
            'body': body}


def echo(event, context):
    return ok(json.dumps(event))
