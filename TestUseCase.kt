import com.xiaojinzi.support.architecture.mvvm1.BaseUseCase
import com.xiaojinzi.support.architecture.mvvm1.BaseUseCaseImpl

interface TestUseCase: BaseUseCase {
}

class TestUseCaseImpl: BaseUseCaseImpl(), TestUseCase {
}