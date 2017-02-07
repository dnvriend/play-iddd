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

import javax.inject.Inject

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.util.Timeout
import com.github.dnvriend.iddd.agile.views.ProductsView
import com.github.dnvriend.iddd.issue.adapters.AgileProductCreatedAdapter
import com.github.dnvriend.iddd.issue.application.IssueApplication
import com.github.dnvriend.iddd.views.AllEventLogger
import com.google.inject.name.Names
import com.google.inject.{ AbstractModule, Provider }
import play.api.libs.concurrent.AkkaGuiceSupport
import play.entity.registry.Registry

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[Timeout]).toInstance(10.seconds)

    // agile views
    bindActor[AllEventLogger]("all-event-logger")
    bindActor[ProductsView]("products-view")

    // issue adapters
    bind(classOf[ActorRef])
      .annotatedWith(Names.named("agile-product-created-adapter"))
      .toProvider(classOf[AgileProductCreatedAdapterProvider])
      .asEagerSingleton()

    // global persistent entity registry
    bindActor[Registry]("registry")
  }
}

class AgileProductCreatedAdapterProvider @Inject() (system: ActorSystem, issueApplication: IssueApplication)(implicit ec: ExecutionContext) extends Provider[ActorRef] {
  override def get(): ActorRef = {
    system.actorOf(Props(classOf[AgileProductCreatedAdapter], issueApplication, ec))
  }
}