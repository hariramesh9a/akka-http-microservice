import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, Logging}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.math._
import spray.json.DefaultJsonProtocol
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{StatusCodes, HttpResponse}
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}
import com.typesafe.config.ConfigFactory


case class ProductInfo(productId: Double, productName: String, productCode: String, releaseDate: String,
                       description: String, price: Double, starRating: Double, imageUrl: String)

final case class ProductDomain(items: List[ProductInfo])


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val productInfoFormat = jsonFormat8(ProductInfo)
  implicit val productDomainFormat = jsonFormat1(ProductDomain) // contains List[Item]
}


trait MyJsonService extends Directives with JsonSupport {
  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  def config: Config

  val logger: LoggingAdapter

  // format: OFF
  val route =
    get {
      pathSingleSlash {
        complete(
          {
          val pdt=ProductInfo(1,
            "Leaf Rake",
            "GDN-0011",
            "March 19, 2016",
            "Leaf rake with 48-inch wooden handle.",
            19.95,
            3.2,
            "https://openclipart.org/image/300px/svg_to_png/26215/Anonymous_Leaf_Rake.png")
        List(pdt)
          }

        ) // will render as JSON
      }
    }
  // format: ON
}


trait CorsSupport {
  lazy val allowedOrigin = {
    val config = ConfigFactory.load()
    val sAllowedOrigin = "*"
    HttpOriginRange.*
  }

  //this directive adds access control headers to normal responses
  private def addAccessControlHeaders: Directive0 = {
    respondWithHeaders(
      `Access-Control-Allow-Origin`(allowedOrigin),
      `Access-Control-Allow-Credentials`(true),
      `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With")
    )
  }

  //this handles preflight OPTIONS requests.
  private def preflightRequestHandler: Route = options {
    complete(HttpResponse(StatusCodes.OK).withHeaders(`Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE)))
  }

  def corsHandler(r: Route) = addAccessControlHeaders {
    preflightRequestHandler ~ r
  }
}


object AkkaHttpMicroservice extends App with MyJsonService with CorsSupport{
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  Http().bindAndHandle(corsHandler(route), config.getString("http.interface"), config.getInt("http.port"))
}
