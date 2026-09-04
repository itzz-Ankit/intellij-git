@ECHO OFF
SETLOCAL

SET "BASE_DIR=%~dp0"
SET "WRAPPER_DIR=%BASE_DIR%.mvn\wrapper"
SET "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
SET "WRAPPER_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.4/maven-wrapper-3.3.4.jar"

IF NOT EXIST "%WRAPPER_JAR%" (
    ECHO Maven Wrapper jar not found. Downloading it...
    powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "$ErrorActionPreference='Stop'; Invoke-WebRequest -UseBasicParsing -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'"
    IF ERRORLEVEL 1 (
        ECHO Failed to download Maven Wrapper. Check your internet connection.
        EXIT /B 1
    )
)

SET "MAVEN_PROJECTBASEDIR=%BASE_DIR:~0,-1%"
SET "JAVA_EXE=java"
IF EXIST "%USERPROFILE%\.jdks\openjdk-26.0.2\bin\java.exe" SET "JAVA_EXE=%USERPROFILE%\.jdks\openjdk-26.0.2\bin\java.exe"

"%JAVA_EXE%" %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
EXIT /B %ERRORLEVEL%
