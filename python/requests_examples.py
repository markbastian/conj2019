import requests as r

response1 = r.get('http://localhost:3000/weapons').json()
print(response1)

response2 = r.get('http://localhost:3000/weapons?name=Opacity').json()
print(response2)

print(response2['Opacity'][0])

response3 = r.get('http://localhost:3000/weapons',
                  params={'name': 'Opacity'}).json()
print(response3)

print(response3['Opacity'][0])

# import requests as r
#
# res = r.get('http://localhost:3000/weapons',
#             params={'name': 'Famine'}).json()
#
# print(res)
#
# # {'Famine': ['Scales']}
