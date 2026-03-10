@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in
@REM compliance with the License.  You may obtain a copy of
@REM the License at
@REM
@REM   http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------
@IF "%__MVNW_ARG0_NAME__%"=="" (SET "MVN_CMD=mvn.cmd") ELSE (SET "MVN_CMD=%__MVNW_ARG0_NAME__%")
@SETLOCAL

set MAVEN_PROJECTBASEDIR=%~dp0

IF NOT "%MVNW_REPOURL%" == "" goto customrepository
SET MVNW_REPOURL=https://repo.maven.apache.org/maven2

:customrepository
SET WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain
SET WRAPPER_URL=%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
SET DOWNLOAD_URL=%MVNW_REPOURL%/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip

FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    IF "%%A"=="distributionUrl" SET DOWNLOAD_URL=%%B
)

SET WRAPPER_JAR_PATH=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar
%JAVA_HOME%\bin\java.exe -cp %WRAPPER_JAR_PATH% %WRAPPER_LAUNCHER% %MAVEN_PROJECTBASEDIR% %*
