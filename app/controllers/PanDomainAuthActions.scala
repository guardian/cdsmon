package controllers

import com.gu.pandomainauth.action.AuthActions
import com.gu.pandomainauth.model.AuthenticatedUser
import play.api.Configuration
import com.amazonaws.auth.{AWSCredentialsProvider, BasicAWSCredentials}

trait PanDomainAuthActions extends AuthActions {

  import play.api.Play.current
  lazy val config:Configuration = play.api.Play.configuration

  override def validateUser(authedUser: AuthenticatedUser): Boolean = {
    (authedUser.user.email endsWith ("@guardian.co.uk")) && authedUser.multiFactor
  }

  override def cacheValidation = true

  override def authCallbackUrl: String = config.getString("host").get + "/oauthCallback"

  override lazy val domain: String = config.getString("pandomain.domain").get

  lazy val awsSecretAccessKey: Option[String] = config.getString("pandomain.aws.secret")
  lazy val awsKeyId: Option[String] = config.getString("pandomain.aws.keyId")
  override lazy val awscredentials =
    for (key <- awsKeyId.get.toString; secret <- awsSecretAccessKey.get.toString)
      yield new BasicAWSCredentials(key, secret)

  override lazy val system: String = "workflow"
}

