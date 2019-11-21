### 使用方法
1. 复制demo中buildSrc目录到项目根目录，如需使用dependencies的形式来加入插件，需要配置maven打包上传    

2. 在主工程的build.gradle文件中```apply plugin: 'com.android.application'```后添加```apply plugin: 'MethodPerforenceAnalysis'```    
如下:    
    
      apply plugin: 'com.android.application'
      
      apply plugin: 'MethodPerformanceAnalysis'
      
      
3. 在主工程的build.gradle文件末尾添加如下配置块:    
    
    methodMonitorExt {
        filterList=[]
        targetList=[]
        className='com/lucius/tec/performance/MethodMonitor'
        methodName='trackMethodPerformance'
    }
       
       