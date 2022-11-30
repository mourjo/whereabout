# whereabout
A service that imports CSV data and exposes it via an API.

The CSV file (`data_dump.csv`) that contains raw geolocation data. The goal is to develop
a service that imports such data and expose it via an API.


## Installation

The code is written assuming it will run on:
```
Leiningen 2.9.8 on Java 18.0.1.1 OpenJDK 64-Bit Server VM
```

This requires the following to be installed:
- [Leiningen](https://formulae.brew.sh/formula/leiningen)
  - On Mac, `brew install leiningen`
- [Java 17+](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
  - On Mac, `brew install java`

## Compilation

Run tests
```
lein do clean, test
```

To compile:
```
lein uberjar
```

To run:
```
java -jar target/whereabout-0.1.0-SNAPSHOT-standalone.jar
```


## System Design
The system is made of components that rely some other components:
![](resources/system.png "System diagram")

- The HTTP Server depends on the database
- The model depends on the file reader and uses the database to insert rows 


The database used is SQLite.

## License

Copyright © 2022 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
