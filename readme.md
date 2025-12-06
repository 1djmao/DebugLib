## 使用
```
	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```

```
dependencies {
	        implementation 'com.github.1djmao:DebugLib:Tag'
	}
```


在 Application 中初始化
```
TestClient.init(this);
TestClient.addSpName("sp1");
TestClient.addSpName("sp2");

TestClient.setDbName("aaaa.db");
```
在需要显示日志的地方调用

```
TestClient.addLog(message);
TestClient.addLog(message,Color.RED);
```
