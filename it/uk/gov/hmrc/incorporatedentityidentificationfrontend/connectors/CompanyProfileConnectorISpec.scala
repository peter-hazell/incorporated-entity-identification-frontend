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

package uk.gov.hmrc.incorporatedentityidentificationfrontend.connectors

import play.api.test.Helpers.{NOT_FOUND, OK, await, defaultAwaitTimeout}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.incorporatedentityidentificationfrontend.assets.TestConstants.{testCompanyName, testCompanyNumber, testDateOfIncorporation, testJourneyId}
import uk.gov.hmrc.incorporatedentityidentificationfrontend.featureswitch.core.config.{CompaniesHouseStub, FeatureSwitching}
import uk.gov.hmrc.incorporatedentityidentificationfrontend.models.CompanyProfile
import uk.gov.hmrc.incorporatedentityidentificationfrontend.stubs.{CompaniesHouseApiStub, IncorporatedEntityIdentificationStub}
import uk.gov.hmrc.incorporatedentityidentificationfrontend.utils.ComponentSpecHelper

class CompanyProfileConnectorISpec extends ComponentSpecHelper with CompaniesHouseApiStub with IncorporatedEntityIdentificationStub with FeatureSwitching {

  private lazy val companyProfileConnector: CompanyProfileConnector = app.injector.instanceOf[CompanyProfileConnector]

  private implicit val headerCarrier: HeaderCarrier = HeaderCarrier()

  "getCompanyProfile" should {
    "return Company profile" when {
      "the companyNumber exists and the feature switch is enabled" in {
        enable(CompaniesHouseStub)
        stubRetrieveCompanyProfileFromStub(testCompanyNumber)(
          status = OK,
          body = companyProfileJson(testCompanyNumber, testCompanyName, testDateOfIncorporation)
        )
        stubStoreCompanyProfile(testJourneyId, CompanyProfile(testCompanyName, testCompanyNumber, testDateOfIncorporation))(status = OK)

        val result = await(companyProfileConnector.getCompanyProfile(testCompanyNumber))

        result mustBe Some(CompanyProfile(testCompanyName, testCompanyNumber, testDateOfIncorporation))
      }
    }
    "return None" when {
      "the companyNumber cannot be found and the feature switch is enabled" in {
        enable(CompaniesHouseStub)
        stubRetrieveCompanyProfileFromStub(testCompanyNumber)(status = NOT_FOUND)

        val result = await(companyProfileConnector.getCompanyProfile(testCompanyNumber))

        result mustBe None
      }
    }
    "return Company Profile" when {
      "the companyNumber exists and the feature switch is disabled" in {
        disable(CompaniesHouseStub)
        stubRetrieveCompanyProfileFromCoHo(testCompanyNumber)(
          status = OK,
          body = companyProfileJson(testCompanyNumber, testCompanyName, testDateOfIncorporation)
        )
        stubStoreCompanyProfile(testJourneyId, CompanyProfile(testCompanyName, testCompanyNumber, testDateOfIncorporation))(status = OK)

        val result = await(companyProfileConnector.getCompanyProfile(testCompanyNumber))

        result mustBe Some(CompanyProfile(testCompanyName, testCompanyNumber, testDateOfIncorporation))
      }
    }
    "return None" when {
      "the companyNumber cannot be found and the feature switch is disabled" in {
        disable(CompaniesHouseStub)
        stubRetrieveCompanyProfileFromCoHo(testCompanyNumber)(status = NOT_FOUND)

        val result = await(companyProfileConnector.getCompanyProfile(testCompanyNumber))

        result mustBe None
      }
    }
  }

}
