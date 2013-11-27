@echo off
echo Test Fallidos:  > TestSummary.txt
for /r %%f in (*.correcto.ceivm) do (	
	:label1
	java -jar CeIVM-cei2011.jar %%f > TestsResults/%%~nxf.TestResult.txt	
	find /c "La ejecución del programa finalizó exitosamente." TestsResults/%%~nxf.TestResult.txt
	if errorlevel 1 echo Test %%~nxf >> TestSummary.txt
)
