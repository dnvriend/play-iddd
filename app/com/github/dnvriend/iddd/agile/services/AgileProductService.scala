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

package com.github.dnvriend.iddd.agile.services

import akka.actor.ReceiveTimeout
import akka.persistence.PersistentActor
import com.github.dnvriend.iddd.DomainEvent
import com.github.dnvriend.iddd.agile.domain.product.{ CreateAgileProduct, AgileProduct }

import scala.concurrent.duration._

class AgileProductService extends PersistentActor {
  override def preStart(): Unit = {
    super.preStart()
    context.setReceiveTimeout(10.seconds)
  }

  override val persistenceId: String = self.path.name
  var state: Option[AgileProduct] = Option.empty[AgileProduct]
  override def receiveRecover: Receive = {
    case event: DomainEvent =>
      state = AgileProduct.handleEvent(state, event)
  }

  override def receiveCommand: Receive = {
    case cmd: CreateAgileProduct =>
      val (newState, event) = AgileProduct.handleCommand(state, lastSequenceNr, cmd)
      persist(event) { event =>
        state = newState
        sender() ! event
      }

    case ReceiveTimeout =>
      context.stop(self)
  }
}
