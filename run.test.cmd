@echo off
echo Test Fallidos:  > TestSummary.txt
for /r %%f in (*.correcto.ceivm) do (	
	:label1
	java -jar CeIVM-cei2011.jar %%f > TestsResults/%%~nxf.TestResult.txt	
	find /c "La ejecuci�n del programa finaliz� exitosamente." TestsResults/%%~nxf.TestResult.txt
	if errorlevel 1 echo Test %%~nxf >> TestSummary.txt
)
