@echo off // isso serve pra nao imprimir os comando do .cmd antes de rodar
setlocal

set MAVEN_VERSION=3.9.9
set MAVEN_HOME=%USERPROFILE%\.m2\apache-maven-%MAVEN_VERSION% // Ao Rodar o projeto, aqui verifica se o Maven esta instalado, caso nn tenha, ele baixa automaticamente
set MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd

if not exist "%MAVEN_CMD%" (
    echo [INFO] Maven nao encontrado. Baixando Maven %MAVEN_VERSION%...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile '%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip'"
    echo [INFO] Extraindo...
    powershell -Command "Expand-Archive -Path '%TEMP%\apache-maven-%MAVEN_VERSION%-bin.zip' -DestinationPath '%USERPROFILE%\.m2' -Force"
    echo [INFO] Maven instalado com sucesso.
)

"%MAVEN_CMD%" %* // %* é um atalho do batch que significa "tudo que o usuário digitou depois do nome do script". Sem ele, o Maven rodaria sem saber o que fazer.

endlocal
