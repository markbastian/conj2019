# conj2019

Material presented at my 2019 Clojure/conj talk, "Defeating the Horsemen of the Coding Apocalypse."

## Actual Code Used/Demoed
The "simple system" demo is located in src/main/clj/conj2019/horsemen/core.clj. It can be launched by switching to that ns and invoking `(start sys)`.

The "EBS/Remote REPL" demo is located in src/main/clj/conj2019/full_demo/system.clj. It can launched by switching to that ns and invoking `(partsbin/start sys)`. The main class used when packaging and deploying to EBS is located at src/main/clj/conj2019/full_demo/core.clj.

There are a handful of other files in the project that didn't make it into the demo. Feel free to poke around. src/main/java/conj2019/horsemen/app/v5 contains a Spring Boot version of the simple system app for comparison, but there wasn't time to discuss it in the talk.

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
   * may need pipenv shell
   * eb ssh (follow directions)
   * eb ssh --custom 'ssh -i ~/.ssh/mykey.pem -L 3001:localhost:3001'
   * http://conj2019-conj2019.us-east-1.elasticbeanstalk.com/
   * lein with-profile +ebs-java beanstalk terminate conj2019

## Data
sls deploy -v
1. Non-stripped down project (upload mid 40MB) 3:13
1. Stripped down 1:15, 1:45

sls deploy function -f EchoHandler
1. 0:33

git push heroku master
~1:30

## Random Links
Mostly for my own reference.

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/) using Spring.
* [The State of Java in 2019](https://www.baeldung.com/java-in-2019)
  * Spring 5 > 50%
  * Spring Boot > 60%
* For Spring
  * https://spring.io/guides/gs/accessing-data-jpa/
  * 
  
## License

Copyright © 2019 Mark Bastian

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
