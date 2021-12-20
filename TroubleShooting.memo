✳ Maven 빌드로 .jar 생성 후 다른 프로젝트에서 import로 사용할 때, poi.jar 사용 못하던 문제
	- 에러내용 : Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/poi/ss/usermodel/Workbook at Runner.MainClass.main(MainClass.java:46)
	Caused by: java.lang.ClassNotFoundException: org.apache.poi.ss.usermodel.Workbook
	at java.net.URLClassLoader.findClass(Unknown Source)
	at java.lang.ClassLoader.loadClass(Unknown Source)
	at sun.misc.Launcher$AppClassLoader.loadClass(Unknown Source)
	at java.lang.ClassLoader.loadClass(Unknown Source)
	... 1 more

	- 사용 dependency :
		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi-ooxml</artifactId>
			<version>4.1.2</version>
		</dependency>

✳ Resolve Step :
	✅ poi-ooxml은 다른 .jar와 다르게 WEB-INF 안에 maven 폴더가 존재하지 않음. (pom.xml, pom.properties)
	1️⃣ build를 할 때 poi.jar가 포함이 안된건지, 아니면 다른 프로젝트에서 import를 사용할 때 제대로 참조를 못하는지 확인해봐야함.
		➡ YONGYVER.jar을 import한 프로젝트가 maven 프로젝트가 아니라서 안된건지 테스트
			❎ maven project로 변환 후 실행했는데 실패
    	➡ 다른 외부 jar (lombok, json)은 잘 동작하는지 테스트
			❎ poi.jar를 사용하지 않는 ObjectToInsertQuery Class로 테스트했으니 Gson Jar에서 NoClassDefError 받음
    	✅ Maven Project에서 사용하던 외부 jar는 Maven Build 시 자동으로 포함되지 않음을 확인.
    	✅ gson.jar는 pom.properties, pom.xml을 포함하지만 동작하지 않음을 확인.
	2️⃣ Maven Build 옵션 중 외부 lib를 포함하도록 하는 방법
		🎉 Maven Build의 plugin 중 "maven-assembly-plugin"에 jar-with-dependencies 를 추가해서 해결.
		- 참고 자료 : https://kogun82.tistory.com/151
		- Maven Buld 시 Goal : assembly:assembly
		- 사용한 Plugin
		<plugins>
			<plugin>
		        <groupId>org.apache.maven.plugins</groupId>
		        <artifactId>maven-assembly-plugin</artifactId>
		        <configuration>
		          	<descriptorRefs>
		            	<descriptorRef>jar-with-dependencies</descriptorRef>
		          	</descriptorRefs>
		        </configuration>
			</plugin>
		</plugins>