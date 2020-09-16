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

package uk.gov.hmrc.incorporatedentityidentificationfrontend.controllers

import play.api.libs.ws.WSResponse
import play.api.test.Helpers._
import uk.gov.hmrc.incorporatedentityidentificationfrontend.assets.TestConstants._
import uk.gov.hmrc.incorporatedentityidentificationfrontend.featureswitch.core.config.{CompaniesHouseStub, FeatureSwitching}
import uk.gov.hmrc.incorporatedentityidentificationfrontend.models.CompanyProfile
import uk.gov.hmrc.incorporatedentityidentificationfrontend.stubs.{CompaniesHouseApiStub, IncorporatedEntityIdentificationStub}
import uk.gov.hmrc.incorporatedentityidentificationfrontend.utils.ComponentSpecHelper
import uk.gov.hmrc.incorporatedentityidentificationfrontend.views.CaptureCompanyNumberTests


class CaptureCompanyNumberControllerISpec extends ComponentSpecHelper
  with CaptureCompanyNumberTests with CompaniesHouseApiStub with IncorporatedEntityIdentificationStub with FeatureSwitching {

  "GET /company-number" should {
    lazy val result: WSResponse = get(s"/$testJourneyId/company-number")

    "return OK" in {
      result.status mustBe OK
    }
    "return a view which" should {
      testCaptureCompanyNumberView(result)
    }
  }

  "POST /company-number" when {
    "the feature switch is enabled" should {
      "retrieve companies house profile from the stub" when {
        "the company number is correct" should {
          "store companies house profile and redirect to the Confirm Business Name page" in {
            enable(CompaniesHouseStub)
            stubRetrieveCompanyProfileFromStub(testCompanyNumber)(status = OK, body = companyProfileJson(testCompanyNumber, testCompanyName))
            stubStoreCompanyProfile(testJourneyId, CompanyProfile(testCompanyName, testCompanyNumber))(status = OK)

            lazy val result = post(s"/$testJourneyId/company-number")(companyNumberKey -> testCompanyNumber)

            result must have(
              httpStatus(SEE_OTHER),
              redirectUri(routes.ConfirmBusinessNameController.show(testJourneyId).url)
            )
          }
        }
      }
    }

    "the feature switch is disabled" should {
      "retrieve companies house profile from coho" when {
        "the company number is correct" should {
          "store companies house profile and redirect to the Confirm Business Name page" in {
            disable(CompaniesHouseStub)
            stubRetrieveCompanyProfileFromCoHo(testCompanyNumber)(status = OK, body = companyProfileJson(testCompanyNumber, testCompanyName))
            stubStoreCompanyProfile(testJourneyId, CompanyProfile(testCompanyName, testCompanyNumber))(status = OK)

            lazy val result = post(s"/$testJourneyId/company-number")(companyNumberKey -> testCompanyNumber)

            result must have(
              httpStatus(SEE_OTHER),
              redirectUri(routes.ConfirmBusinessNameController.show(testJourneyId).url)
            )
          }
        }

        "the company number is missing" should {
          lazy val result = post(s"/$testJourneyId/company-number")(companyNumberKey -> "")
          "return a bad request" in {
            result.status mustBe BAD_REQUEST
          }
          testCaptureCompanyNumberEmpty(result)
        }

        "the company number is not found" should {
          "throw an internal server error" in {
            stubRetrieveCompanyProfileFromCoHo(testCompanyNumber)(status = NOT_FOUND)
            lazy val result = post(s"/$testJourneyId/company-number")(companyNumberKey -> testCompanyNumber)

            result.status mustBe INTERNAL_SERVER_ERROR

          }
        }

        "the company number has more than 8 characters" should {
          lazy val result = post(s"/$testJourneyId/company-number")(companyNumberKey -> "0123456789")
          "return a bad request" in {
            result.status mustBe BAD_REQUEST
          }
          testCaptureCompanyNumberWrongLength(result)
        }

        "company number is not in the correct format" should {
          lazy val result = post(s"/$testJourneyId/company-number")(companyNumberKey -> "13E!!!%")
          "return a bad request" in {
            result.status mustBe BAD_REQUEST
          }
          testCaptureCompanyNumberWrongFormat(result)
        }

      }
    }
  }

}
