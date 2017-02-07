/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package play.entity.registry

import javax.inject.{ Inject, Singleton }

import akka.actor.{ Actor, ActorRef, Props }
import akka.pattern.ask
import akka.persistence.PersistentActor
import akka.util.Timeout
import play.entity.registry.Registry.{ RefForRequest, RefForResponse }
import com.google.inject.name.Named

import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect.ClassTag

object Registry {
  final case class RefForRequest[A <: PersistentActor](ct: ClassTag[A], entityId: String)
  final case class RefForResponse(ref: ActorRef)
}

class Registry extends Actor {
  override def receive: Receive = {
    case RefForRequest(ct, entityId) =>
      val className = ct.runtimeClass
      val name = s"${className.getSimpleName}-$entityId"
      sender() ! RefForResponse(context.child(name).getOrElse(context.actorOf(Props(className), name)))
  }
}

@Singleton
class PersistentEntityRegistry @Inject() (@Named("registry") registry: ActorRef)(implicit timeout: Timeout, ec: ExecutionContext) {
  def refFor[M <: PersistentActor](entityId: String)(implicit ct: ClassTag[M]): Future[ActorRef] =
    (registry ? RefForRequest(ct, entityId)).mapTo[RefForResponse].map(_.ref)
}
