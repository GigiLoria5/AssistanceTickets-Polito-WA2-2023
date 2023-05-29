package it.polito.wa2.g13.server.observer.aop

import io.micrometer.observation.Observation
import io.micrometer.observation.ObservationHandler
import io.micrometer.observation.aop.ObservedAspect.ObservedAspectContext


open class AbstractObserveAroundMethodHandler : AbstractLogAspect(), ObservationHandler<ObservedAspectContext> {
    override fun onStart(context: ObservedAspectContext) {
        val joinPoint = context.proceedingJoinPoint
        super.logBefore(joinPoint)
    }

    override fun onStop(context: ObservedAspectContext) {
        val joinPoint = context.proceedingJoinPoint
        super.logAfter(joinPoint)
    }

    override fun supportsContext(context: Observation.Context): Boolean {
        return context is ObservedAspectContext
    }
}
