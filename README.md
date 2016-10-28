# RestfulAutomationTest
***
Set up an Automation interface Test for Restful based on Maven project. 

Test steps: 

1. Import this maven project on your myEclipse or IDEA.
2. Open RestFullAPI\src\test\resources\Http_Request_Data.xlsx, in the first sheet, please fill your POST/GET http Request you would like, and then switch to the second sheet named "baseline", fill the response result you expect!!!  

**Note:** 
You will find other logic code in such project, which is a new thread for TCP setup. It's used for sending Hex commands to TCP clients, and receiving commands from clients, e.g. A server connects to a vehicle over the OTA protocal(TCP). So if your project has the similar behaviors you could consider such part, hope this automation test framework including HTTP and TCP validation could help you!  
Other details, please look into the code and change the code to meet your automation test. 

;) Enjoy

中文部分：  
此测试框架，能最大可能的帮你实现基于RESTFul的接口自动化测试。  
只需要在Http_Request_Data.xlsx填入自己需要POST或GET的IP, 端口，内容，然后用TestNG去执行自己的测试数据，就可以完成基于接口的自动化测试。  

**注意：**  
此测试代码里，有一些关于目前业务的逻辑，比如，我启动了TCP建立连接的线程去接收空口（车辆和监控平台的建链是采用OTA标准）来的消息。如果你的项目中，也有类似的情况，可以参考代码。  
希望这套测试代码，可以帮你提高测试效率，因为它可以通用到任何一个RESTFul风格的Server测试。
