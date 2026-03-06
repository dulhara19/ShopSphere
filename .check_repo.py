import urllib.request, urllib.error

url = 'https://github.com/dulhara19/ShopSphere'
try:
    r = urllib.request.urlopen(url)
    print('STATUS', r.getcode())
except urllib.error.HTTPError as e:
    print('HTTP', e.code, e.reason)
except Exception as e:
    print('ERR', e)
