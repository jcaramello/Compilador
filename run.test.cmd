for /r %%f in (*.ceivm) do (	
	java -jar CeIVM-cei2011.jar %%f > TestsResults/%%~nxf.TestResult.txt
	)

