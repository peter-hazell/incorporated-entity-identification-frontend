/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.incorporatedentityidentificationfrontend.services

import javax.inject.Inject
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incorporatedentityidentificationfrontend.connectors.JourneyConnector
import uk.gov.hmrc.incorporatedentityidentificationfrontend.models.JourneyConfig
import uk.gov.hmrc.incorporatedentityidentificationfrontend.repositories.JourneyConfigRepository

import scala.concurrent.{ExecutionContext, Future}

class JourneyService @Inject()(journeyConnector: JourneyConnector,
                               journeyConfigRepository: JourneyConfigRepository){
  def createJourney(journeyConfig: JourneyConfig)(implicit headerCarrier: HeaderCarrier, ec: ExecutionContext): Future[String] =
    for {
      journeyId <- journeyConnector.createJourney()
      _ <- journeyConfigRepository.insertJourneyConfig(journeyId, journeyConfig)
    } yield journeyId
}