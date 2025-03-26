import requests

owner = "jlefur"
repo = "MelaneSim_2024"
url = f"https://api.github.com/repos/{owner}/{repo}/commits"
headers = {"Accept": "application/vnd.github.v3+json"}

response = requests.get(url, headers=headers)

print(f"Status code: {response.status_code}")

if response.status_code != 200:
    print("Erreur API :")
    print(response.text)
else:
    commits = response.json()
    for commit in commits:
        sha = commit['sha']
        message = commit['commit']['message']
        author = commit['commit']['author']['name']
        date = commit['commit']['author']['date']
        print(f"Commit: {sha}")
        print(f"Auteur: {author}")
        print(f"Date: {date}")
        print(f"Message:\n{message}\n{'-'*50}")
