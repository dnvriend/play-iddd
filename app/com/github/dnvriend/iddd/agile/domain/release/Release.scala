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

package com.github.dnvriend.iddd.agile.domain.release

import java.util.{ Date, UUID }

import com.github.dnvriend.iddd.{ DomainCommand, DomainEvent }
import com.github.dnvriend.iddd.agile.domain.product.{ ProductId, SeverityTotals, SeverityWeights }

trait Measurement {
  def value: Double
}
final case class KLOCMeasurement(value: Double) extends Measurement
final case class PreferredMeasurements(backlogItemsMeasurement: BacklogItemsMeasurement, classesMeasurement: ClassesMeasurement, klocMeasurement: KLOCMeasurement)
final case class DefectDensity(date: Date, indexFigure: Double, measurement: Measurement)
final case class BacklogItemsMeasurement(value: Int)
final case class ClassesMeasurement(value: Int)
final case class ReleaseId(id: String)
object ReleaseId {
  def apply(): ReleaseId = ReleaseId(UUID.randomUUID.toString)
}
final case class DefectDensitiesCalculator(severityTotals: SeverityTotals, severityWeights: SeverityWeights)
final case class Release(
  defectDensityHistory: List[DefectDensity],
  description: String,
  name: String,
  preferredMeasurements: PreferredMeasurements,
  productId: ProductId,
  releaseId: ReleaseId
)

object Release {
  def handleCommand(state: Option[Release], cmd: DomainCommand): (Option[Release], DomainEvent) = ???

  def handleEvent(state: Option[Release], event: DomainEvent): Option[Release] = ???
}
