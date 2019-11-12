# conj2019

A Clojure library designed to ... well, that part is up to you.

## Links

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/) using Spring.
* [The State of Java in 2019](https://www.baeldung.com/java-in-2019)
  * Spring 5 > 50%
  * Spring Boot > 60%
* For Spring
  * https://spring.io/guides/gs/accessing-data-jpa/
  * 

## Crib Sheet
* Heroku
   * git push heroku master
   * heroku ps:scale web=1 (make sure you have 1 dyno)
   * heroku open (https://fierce-eyrie-72312.herokuapp.com)
   * heroku ps:scale web=0 (shut it all down)
   * heroku logs --tail
   * heroku ps:forward 3001
   
* EBS
   * lein deploy-ebs-java
   * eb ssh (follow directions)
   * eb ssh --custom 'ssh -i ~/.ssh/mykey.pem -L 3001:localhost:3001'
   * lein with-profile +ebs-java beanstalk terminate conj2019

## Data
sls deploy -v
1. Non-stripped down project (upload mid 40MB) 3:13
1. Stripped down 1:15, 1:45

sls deploy function -f EchoHandler
1. 0:33

git push heroku master
~1:30

## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
