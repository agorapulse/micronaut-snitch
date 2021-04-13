# Micronaut Snitch

[![Build Status](https://github.com/agorapulse/micronaut-snitch/workflows/Check/badge.svg)](https://github.com/agorapulse/micronaut-snitch/actions)
[![Download](https://api.bintray.com/packages/agorapulse/libs/micronaut-snitch/images/download.svg)](https://bintray.com/agorapulse/libs/micronaut-snitch/_latestVersion)
[![Coverage Status](https://coveralls.io/repos/github/agorapulse/micronaut-snitch/badge.svg?branch=master)](https://coveralls.io/github/agorapulse/micronaut-snitch?branch=master)

Micronaut Snitch

See [Full Documentation][DOCS]

[DOCS]: https://agorapulse.github.io/micronaut-snitch


## Next Steps (To Be Deleted)

This project uses GitHub Actions which to run check for every build and to publish to BinTray and GitHub Pages on each release. Follow the steps to complete the setup.

### Create a new repository on GitHub 
[Create a new repository on GitHub][1] `micronaut-snitch` for organization `agorapulse`

### Add the repository to Coveralls.io

[Add the repository to Coveralls.io][2]

### Create Secrets
[Create the following secrets on GitHub][3]:

`BINTRAY_USER` - Username of a BinTray user with write priviledges to https://bintray.com/agorapulse/libs/

`BINTRAY_KEY` - Access key with write priveledges to https://bintray.com/agorapulse/libs/

`COVERALLS_REPO_TOKEN` - Coveralls token which is available at https://coveralls.io/github/agorapulse/micronaut-snitch

`PERSONAL_TOKEN` - GitHub repository token with `repo` priviledges for https://github.com/agorapulse/micronaut-snitch/
 
### Initialize Guide and License Headers

```
./gradlew initGuide licenseFormat
```
 
### Init Git repository in the root of this project, commit everything and push to GitHub:
  
```
git init
git add -A
git commit -m "Initial commit"
git remote add origin git@github.com:agorapulse/micronaut-snitch.git
git push -u origin master
```

### Cleanup

Following steps will commit the cleaned up README file. You can copy them and keep in the clipboard. 
Then paste the snippet into the terminal once the clean up is finished. 
```
git add README.md
git commit -m "Cleaned up README.md"
git push origin master
```

Delete the **Next Steps** section of this README file once it is no longer required and push again 

[1]: https://github.com/new
[2]: https://coveralls.io/repos/new
[3]: https://github.com/agorapulse/micronaut-snitch/settings/secrets
