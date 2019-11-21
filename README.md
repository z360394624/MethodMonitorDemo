### 使用方法
1. 复制demo中buildSrc目录到项目根目录，如需使用dependencies的形式来加入插件，需要配置maven打包上传    

2. 在主工程的build.gradle文件中```apply plugin: 'com.android.application'```后添加```apply plugin: 'MethodPerforenceAnalysis'```    
如下:    
    
    
        apply plugin: 'com.android.application'
        
        apply plugin: 'MethodPerformanceAnalysis'
      
      
3. 在主工程的build.gradle文件末尾添加如下配置块:    
      
        
        methodMonitorExt {
        
            // 过滤列表，不需要进行方法执行时间统计的包名，可选 eg: filterList=['android.support','androidx.appcompat']
            filterList=[]
            // 目标列表，需要进行方法执行时间统计的包名，可选 eg: filterList=['com.lucius','com.integer']，可选
            targetList=[]
            
            // 执行统计的类名，必填
            className='com/lucius/tec/performance/MethodMonitor'
            // 执行统计的方法名，必填，实现见demo的MethodMonitor.java，
            // 方法名和类名无限制
            // 方法参数第一个必须为String类型，是方法名；第二个为long类型，是方法开始执行的时间，单位毫秒
            methodName='trackMethodPerformance'
        }
        
4. 方法执行时间统计结果的持久化与上报需要自行实现，目前仅有log输出
        
        
### 基本原理

自定义Transform，在class文件打包成dex之前，使用ASM遍历所有class文件的方法，在方法的的开头和结尾插入代码来统计方法的执行时间    
ASM本质是通过插入jvm指令来修改class字节码文件


### 注意事项

仅限于测试期间进行统计，方法执行时间统计结果持久化与上报必然会消耗较多的io和内存；  
此插件使用专门用于方法执行时间统计，不建议与Dexguard同时使用；      
如需和Dexguard同时使用，需要在进行混淆之前进行代码插入

       
       