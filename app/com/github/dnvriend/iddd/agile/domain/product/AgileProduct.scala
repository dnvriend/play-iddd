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

package com.github.dnvriend.iddd.agile.domain.product

import com.github.dnvriend.iddd.{ DomainCommand, DomainEvent }

import scala.compat.Platform

// commands
final case class CreateAgileProduct(name: String, description: String, productManagerId: String, issueAssignerId: String, productId: String) extends DomainCommand

// events
final case class AgileProductCreated(name: String, description: String, productManagerId: String, issueAssignerId: String, productId: String, occurredOn: Long, eventVersion: Long) extends DomainEvent

// domain
final case class ProductId(id: String)
final case class Release()
final case class IssueAssigner(issuesAssignerId: String)
final case class ProductManager(productManagerId: String)
final case class ProductDefectiveness(defectRanking: Double, name: String, productId: ProductId)

final case class SeverityDeltas(highDelta: Int, lowDelta: Int, mediumDelta: Int)
final case class SeverityWeights(highWeight: Double, lowWeight: Double, mediumWeight: Double)
final case class SeverityTotals(totalHigh: Int, totalLow: Int, totalMedium: Int)

final case class AgileProduct(
  currentRelease: Option[Release],
  description: String,
  issueAsigner: IssueAssigner,
  name: String,
  productId: ProductId,
  productManager: ProductManager,
  severityTotals: Option[SeverityTotals]
)

object AgileProduct {
  def handleCommand(state: Option[AgileProduct], lastSequenceNr: Long, cmd: DomainCommand): (Option[AgileProduct], DomainEvent) = cmd match {
    case CreateAgileProduct(name, description, productManagerId, issueAssignerId, productId) =>
      val product = AgileProduct(None, description, IssueAssigner(issueAssignerId), name, ProductId(productId), ProductManager(productManagerId), None)
      val event = AgileProductCreated(name, description, productManagerId, issueAssignerId, productId, Platform.currentTime, lastSequenceNr + 1)
      (Option(product), event)
  }

  def handleEvent(state: Option[AgileProduct], event: DomainEvent): Option[AgileProduct] = event match {
    case AgileProductCreated(name, description, productManagerId, issueAssignerId, productId, timestamp, eventVersion) =>
      Option(AgileProduct(None, description, IssueAssigner(issueAssignerId), name, ProductId(productId), ProductManager(productManagerId), None))
  }
}
