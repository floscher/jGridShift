#jGridShift

## Building from source
Run
```shell
./gradlew build
```
to produce `build/distributions/jgridshift-x.y.zip`, `build/libs/jgridshift-api-x.y.jar` and `build/libs/jgridshift-gui-x.y.jar`.

If you want to open the project in Eclipse, first run
```shell
./gradlew cleanEclipse eclipse
```
Then you can import it as if it was a normal Eclipse project.

If you want javadoc, run:
```shell
./gradlew javadoc
```
