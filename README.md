# Lonelysword

### 架构
到底什么是架构，架构就是用架子构造出来的东西，算了以我现在的水平还不能告诉你,还不够格，哈哈。我能告诉你自己在学习过程中的领悟，我想架构就是一种约定，怎样的约定？为了更好的研发效率、扩展性和稳定性，更精准的风险防控能力。这样说可能有点抽象。比如我用完东西可能一直放在我喜欢的地方，只是在其他人眼中显得有点乱，在我眼里是很有规律的。因为我一直遵循着这个规定，每本书都有我给它定义的位置。但是我老妈不知道啊，虽然整理的很干净，但是书放错了位置，老妈可能是无意识的，就算下次我问她，她也记不清了。为了找到这本书，我也只能遍历一遍了。如果我们双方都遵循我们俩一起定义的一个规定，一个约定，达成一种共识。那么就很简单了，书永远在那，就算是添新书还是移除，双方都有章可循。

由此可见，架构的好处就是除了引进新的技术，自己或者说产品本身都有更好的体验外，还由于做了统一规范，让结构更加清晰，健壮，团队协作更加方便。

为了让项目架构变得更加清晰，Lonelysword引用了Clean的中心思想，让数据层、领域层和展现层变的更加清晰，防止程序猿们在茫茫的代码海洋里游荡；又或为了让开发人员能够快速的编写动态的业务代码，让编码效率倍增，Lonelysword引用了MVVM架构，防止反复的编写固定代码，然而Lonelysword的优势不止于此：
1. @UseCase注解：省略了Clean架构编写UseCase类代码，若使用Retrofit网络框架可结合请求接口类同时使用。
2. @DaggerApplication注解：可以让dagger2支持sdk开发，使集成sdk的项目不要使用dagger2裤子。
3. @Dagger注解：省略android四大组件注入环中module类的编写。
4. @RootView、@AfterViews、@BeforeViews:省略了activity,fragment初始化视图的过程。

### @UseCase

#### 初识@UseCase
@UseCase注解可以让我们在构建Clean架构的时候，省略编写UseCase的代码，使用在数据仓库接口类或者方法上，它是怎么做到的呢？先看一段代码：


```
    @UseCase
    Flowable<String> getUser(String params);
```
通过以上代码，lonelysword会生成如下代码：

```
public final class GetUserUseCase extends UseCase<Flowable<String>, String> {
  private MainRepository repository;

  @Inject
  public GetUserUseCase(MainRepository repository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);
    this.repository = repository;
  }

  @Override
  protected Flowable<String> buildUseCaseObservable(String request) {
    return this.repository.getUser(request).subscribeOn(Schedulers.from(threadExecutor)).observeOn(postExecutionThread.getScheduler());
  }
}
```

我们能看出注解生成了一个[方法名首字母大写+UseCase]的实体类，省去了我们在使用Clean框架的时候UseCase代码的编写，详细的demo演示请参考：

[https://github.com/Timper-yang/lonelysword](https://github.com/Timper-yang/lonelysword)

#### @UseCase特性
为了更方便@UseCase的使用，我们在该注解加了一些特性，来看以下代码：
    
```
@UseCase(ignore = BaseResponse.class, transformer = ErrorTransformer.class)
public interface MainRepository {

    Flowable<BaseResponse<String>> getUser(String params);

    Completable getUsers(String params);

    @UseCase(ignore = BaseResponse.class, transformer = SigleErrorTransformer.class)
    Single<BaseResponse<String>> getUsers3(String params);
}
```

@UseCase可以使用在接口类之上，使用在类上代表所有的方法都需要生成UseCase类
@UseCase有两个参数： igonre代表返回类型里剔除哪一个类，transformer代表将原始返回类型返回到剔除后返回类型的转换所需的类，no code say JB,我们看以下代码：

注解方法
```
@UseCase(ignore = BaseResponse.class, transformer = SigleErrorTransformer.class)
Single<BaseResponse<String>> getUsers3(String params);
```
生成的实体类
```
public final class GetUsers3UseCase extends UseCase<Single<String>, String> {
  private MainRepository repository;

  @Inject
  public GetUsers3UseCase(MainRepository repository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);
    this.repository = repository;
  }

  @Override
  protected Single<String> buildUseCaseObservable(String request) {
    return this.repository.getUsers3(request).subscribeOn(Schedulers.from(threadExecutor)).observeOn(postExecutionThread.getScheduler()).compose(new SigleErrorTransformer());
  }
}
```
我们看一下当@UseCase 加上了ignore为BaseResponse.class时，生成的类并返回值并没有BaseResponse，而且我们在buildUseCaseObservable方法返回的时候加上了compose(new SigleErrorTransformer())对返回类型进行了变换操作。详细的demo演示请参考：

[https://github.com/Timper-yang/lonelysword](https://github.com/Timper-yang/lonelysword)

#### @UseCase结合retrofit
如果我们的项目数据源只来自retrofit，没有其他的数据来源是，我们可以和使用retrofit的接口类一起使用，如下：

```
@UseCase(ignore = BaseResponse::class, transformer = ErrorTransformer::class)
interface MainService {

    @GET("/article/list/0/json")
    fun getArticles() : Flowable<BaseResponse<BaseList<Article>>>


    @GET("/article/list/0/json?cid=60")
    fun getTops() : Flowable<BaseResponse<BaseList<Article>>>
}
```
像这样，大家发现了没，lonelysword是支持kotlin开发的哦，详细的demo演示请参考：

[https://github.com/Timper-yang/lonelysword](https://github.com/Timper-yang/lonelysword)

## @Dagger

Dagger2 确实比较难学，我想每个开发者学习的时候总是经历了一番痛苦的挣扎过程，于是就有了所谓的从入门到放弃之类的玩笑，当然不排除基础好的同学能够一眼看穿。本文的目的简化dagger2的使用，如果对Dagger2不了解的同学，请私自去了解清楚Dagger2的思想。

本文的@Dagger注解主要是省略四大组件使用Dagger2时Module的编写工作，@Dagger可以使用在Activity、Fragmeng以及任何可注入且非接口的实体类当中，来看以下代码

当@Dagger注入以下Activity和Fragmeng
```
@Dagger
@RootView(R2.layout.act_garden)
class GardenActivity : AppActivity<GardenViewModel, ActGardenBinding>() {
...
}

@Dagger(GardenActivity::class)
@RootView(R2.layout.frg_main)
class MainFragment : AppFragment<MainViewModel,FrgMainBinding>() {
...
}

@Dagger(GardenActivity::class)
@RootView(R2.layout.frg_list)
class ListFragment : AppFragment<ListViewModel,FrgListBinding>() {
...
}
```
Lonelysword会生成以下文件：

```
@Module
public abstract class GardenModule {
  @Binds
  public abstract FragmentActivity provideGardenActivity(GardenActivity param);
}

@Module
public abstract class GardenSubModule {

  @ContributesAndroidInjector
  public abstract ListFragment bindListFragment();

  @ContributesAndroidInjector
  public abstract MainFragment bindMainFragment();
}
```
当然每一个module都会有一个统一生成类来集中这里生产的Module,如下:

```
@Module
public abstract class AppModule$$module {
  @ActivityScope
  @ContributesAndroidInjector(
      modules = {
          GardenModule.class,
          GardenSubModule.class
      }
  )
  abstract GardenActivity bindGardenActivity();
}
```
这样我们只需要在最外层的连接器Component中添加上这个module类即可，如下：

```
@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class, AppModule$$module.class
})
public interface ModuleComponent extends AndroidInjector<ModuleApplication> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<ModuleApplication> {
    }
}
```
如上，我们省去了编写四大组件的注入工作，让dagger2开发过程中如虎添翼，详细的demo演示请参考：

[https://github.com/Timper-yang/lonelysword](https://github.com/Timper-yang/lonelysword)


## @DaggerApplication

使用dagger2必须要在主工程添加相关application注入代码，才能使得dagger2正常工作，但是我们在做sdk开发过程中，又想使用dagger2神器，但是接入sdk方并不想使用dagger2的时候，我们无法控制接入方代码编写。借助ARoute的核心思想，Lonelysword使用@DaggerApplication这个注解可以解决这个问题：

lonelysword提供了DaggerMultiModule类来支持dagger2的模块化，每一个module就像一个单独的应用，先看如下代码
```
DaggerApplication
public class ModuleApplication extends DaggerMultiModule {
    @Override
    protected AndroidInjector<? extends DaggerMultiModule> applicationInjector() {
        return DaggerModuleComponent.builder().create(this);
    }
}
```
并且在主工程的application的onCreate()方法中进行初始化，如下：

```
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Lonelysword.init(this);
    }
}
```
这样module和主工程就没有任何的注入依赖关系，依赖注入完全有module自己来控制，详细的demo演示请参考：

[https://github.com/Timper-yang/lonelysword](https://github.com/Timper-yang/lonelysword)

## @RootView、@AfterViews、@BeforeViews

还记得在使用android开发过程中，为什么要使用Butterknife么？依赖注入，让你无需编写类似findviewById之类的代码，在2015年google官方的mvvm架构databinding问世的后，Batabinding终究要替代Butterknife的工作,如今Batabinding已经很成熟，也没有之前那样有问题特别难找的这种情况，但是Butterknife的核心思想是永存的，Lonelysword同样也应用了借助了Butterknife的思想，使得在使用Databingding架构是如虎添翼

Lonelysword结合databinding编写了AppActivity、AppFragment、AppDialog基类组件，让databinding使用起来更加便捷、简单，如下代码：
```
@Dagger
@RootView(R2.layout.act_garden)
class GardenActivity : AppActivity<GardenViewModel, ActGardenBinding>() {
    @AfterViews
    internal fun view() {
        ...
    }
    @BeforeViews
    internal fun beforeViews() {
        ...
    }
}
```
Lonelysword生成如下代码：

```
public final class LonelySword_GardenActivity implements Unbinder, DefaultLifecycleObserver {
  private GardenActivity target;

  @UiThread
  public LonelySword_GardenActivity(GardenActivity target) {
    this.target = target;
    target.getLifecycle().addObserver(this);;
  }

  @Override
  public View initViews(View container) {
    target.binding = DataBindingUtil.setContentView(target, R.layout.act_garden);
    target.binding.setVariable(BR.view,target);
    target.binding.setVariable(BR.viewModel,target.viewModel);
    return target.binding.getRoot();
  }

  @Override
  public void onCreate(LifecycleOwner owner) {
    target.viewModel = ViewModelProviders.of(target,target.factor).get(GardenViewModel.class);initViews(null);
    target.view$module_debug();
    target.viewModel.afterViews();

  }

  @Override
  public void onResume(LifecycleOwner owner) {
    target.viewModel.onResume();
  }

  @Override
  public void onStart(LifecycleOwner owner) {
    target.viewModel.onStart();
  }

  @Override
  public void onPause(LifecycleOwner owner) {
    target.viewModel.onPause();
  }

  @Override
  public void onStop(LifecycleOwner owner) {
    target.viewModel.onStop();
  }

  @Override
  public void onDestroy(LifecycleOwner owner) {
    target.viewModel.onDestroy();
  }

  @Override
  public void unbind() {
  }
}
```
如上所示：Lonelysword生成类做了databinding中View的初始化工作，并使用了jetpack中的Lifecycle，使得View层的代码更加简单高效，同事viewModel也相应的有对应的生命周期。详细的demo演示请参考：

[https://github.com/Timper-yang/lonelysword](https://github.com/Timper-yang/lonelysword)
    
    
## 总结和源码

- 本篇博文需要在熟练掌握MVVM架构思想、Clean架构思想以及Dagger2的工作原理的情况之下，来阅读和使用本框架，Lonelysword译名独孤剑，以快为目的，让我们在做码农的时候，不要把全部精力铺在业务代码上去，也应该抽出时间来改造我们的武器库，有无武器我们才能更好更快的应付业务需求，这样才能达到一个良性循环。如果你喜欢本文，就来支持一下给一个星哦，感谢大家。
- 源码地址[https://github.com/Timper-yang/lonelysword](https://github.com/Timper-yang/lonelysword)
