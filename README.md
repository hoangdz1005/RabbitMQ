# RabbitMQ - Spring Boot 3.0
### RabbitMQ là gì?
- Là một mesage Broker (MOM – Message-Oriented Middleware), sử dụng giao thức AMQP (Advanced Message Queue Protocol).
- Là một phần mềm trung gian được sử dụng để trao đổi dữ liệu giữa các process, application, system hoặc server. RabbitMQ sẽ nhận message đến từ các thành phần khác nhau trong hệ thống, lưu trữ chúng an toàn trước khi đẩy đến đích.<br>
  [![RabbitMQ](https://gpcoder.com/wp-content/uploads/2020/04/rabbitmq-logo.png?v=1589390970)](https://nodesource.com/products/nsolid)

### Tại sao sử dụng RabbitMQ?
Giả sử chúng ta có một web application cho phép user đăng ký thông tin. Sau khi đăng ký, hệ thống sẽ xử lý thông tin, generate PDF và gửi email lại user. Những công việc này hệ thống cần nhiều thời gian để xử lý, chẳng hạn. Nếu xử lý theo cách thông thường thì user sẽ phải chờ đến khi hệ thống xử lý hoàn thành, và nếu hệ thống có hàng nghìn user truy cập cùng lúc sẽ gây quá tải server.
Đối với những loại công việc thế này, chúng ta sẽ xử lý nó bằng cách sử dụng message queue: Sau khi user nhập đầy đủ các thông tin trên web interface, web application sẽ tạo một Message “Generate PDF” chứa đầy đủ thông tin cần thiết và gửi nó vào Queue của RabbitMQ.<br>
[![RabbitMQ](https://gpcoder.com/wp-content/uploads/2020/04/rabbitmq-workflow-example-1.png?v=1589390957)](https://nodesource.com/products/nsolid)
<br>
**Architecture cơ bản của message queue:**<br><br>
`Producer` : là ứng dụng client, tạo message và publish tới broker.<br><br>
`Consumer` : là ứng dụng client khác, kết nối đến queue, subscribe (đăng kí) và xử lý (consume) message.<br><br>
`Broker (RabbitMQ)` : nhận message từ Producer, lưu trữ chúng an toàn trước khi được lấy từ Consumer.<br><br>

Flow đầy đủ sử dụng RabbitMQ để giải quyết vấn đề trên:
[![RabbitMQ2](https://gpcoder.com/wp-content/uploads/2020/04/rabbitmq-workflow-example-2-1536x731.png?v=1589390956)](https://nodesource.com/products/nsolid)
- (1) User gửi yêu cầu tạo PDF đến web application.
- (2) Web application (Producer) gửi tin nhắn đến RabbitMQ bao gồm dữ liệu từ request như tên và email.
- (3) Một Exchange chấp nhận các tin nhắn từ Producer và định tuyến chúng đến Queue (hàng đợi) để tạo PDF.
- (4) Ứng dụng xử lý PDF (Consumer) nhận Message từ Queue và bắt đầu xử lý PDF.

_Một số vấn đề khác:_
- Đối với các hệ thống sử dụng kiến trúc microservice thì việc gọi chéo giữa các service khá nhiều, khiến cho luồng xử lý khá phức tạp.
- Mức độ trao đổi data giữa các thành phần tăng lên khiến cho việc lập trình trở nên khó khăn hơn (vấn đề maintain khá đau đầu).
- Khi phát triển ứng dụng làm sao để các lập trình viên tập trung vào các domain, business logic thay vì các công việc trao đổi ở tầng infrastructure.
- Với các hệ thống phân tán, khi việc giao tiếp giữa các thành phần với nhau đòi hỏi chúng cần phải biết nhau. Nhưng điều này gây rắc rối cho việc viết code. Một thành phần phải biết quá nhiều dẫn đến rất khó maintain, debug.
### Một số khái niệm trong RabbitMQ:<br><br>
`Producer`: Ứng dụng gửi message.<br><br>
`Consumer`: Ứng dụng nhận message.<br><br>
`Queue`: Lưu trữ messages.<br><br>
`Message`: Thông tin truyền từ Producer đến Consumer qua RabbitMQ.<br><br>
`Connection`: Một kết nối TCP giữa ứng dụng và RabbitMQ broker.<br><br>
`Channel`: Một kết nối ảo trong một Connection. Việc publishing hoặc consuming message từ một queue đều được thực hiện trên channel.<br><br>
`Exchange`: Là nơi nhận message được publish từ Producer và đẩy chúng vào queue dựa vào quy tắc của từng loại Exchange. Để nhận được message, queue phải được nằm (binding) trong ít nhất 1 Exchange.<br><br>
`Binding`: là quy tắc (rule) mà Exchange sử dụng để định tuyến Message đến Queue. Đảm nhận nhiệm vụ liên kết giữa Exchange và Queue.<br><br>
`Routing key`: Một key mà Exchange dựa vào đó để quyết định cách để định tuyến message đến queue. Có thể hiểu nôm na, Routing key là địa chỉ dành cho message.<br><br>
`AMQP` (Advance Message Queuing Protocol): là giao thức truyền message được sử dụng trong RabbitMQ.<br><br>
`User`: Để có thể truy cập vào RabbitMQ, chúng ta phải có username và password. Trong RabbitMQ, mỗi user được chỉ định với một quyền hạn nào đó. User có thể được phân quyền đặc biệt cho một Vhost nào đó.<br><br>
`Virtual host/ Vhost`: Cung cấp những cách riêng biệt để các ứng dụng dùng chung một RabbitMQ instance. Những user khác nhau có thể có các quyền khác nhau đối với vhost khác nhau. Queue và Exchange có thể được tạo, vì vậy chúng chỉ tồn tại trong một vhost.<br><br>
### RabbitMQ hoạt động như thế nào?
[![RabbitMQ2](https://gpcoder.com/wp-content/uploads/2020/04/rabbitmq-flow.png?v=1589390968)](https://nodesource.com/products/nsolid)
- (1) Producer đẩy message vào Exchange. Khi tạo Exchange, bạn phải mô tả nó thuộc loại gì. Các loại Exchange sẽ được giải thích phía dưới.
- (2) Sau khi Exchange nhận Message, nó chịu trách nhiệm định tuyến message. Exchange sẽ chịu trách nhiệm về các thuộc tính của Message, ví dụ routing key, loại Exchange.
- (3) Việc binding phải được tạo từ Exchange đến Queue (hàng đợi). Trong trường hợp này, ta sẽ có hai binding đến hai hàng đợi khác nhau từ một Exchange. Exchange sẽ định tuyến Message vào các hàng đợi dựa trên thuộc tính của của từng Message.
- (4) Các Message nằm ở hàng đợi đến khi chúng được xử lý bởi một Consumer.
- (5) Consumer xử lý Message nhận từ Queue.

### Exchange và các loại Exchange
Message không được publish trực tiếp vào Queue; thay vào đó, Producer gửi message đến Exchange.
Exchange là nơi mà các message được gởi. Exchange nhận tin nhắn và định tuyến nó đến 0 hoặc nhiều Queue với sự trợ giúp của các ràng buộc (binding) và các khóa định tuyến (routing key).
Thuật toán định tuyến được sử dụng phụ thuộc vào loại Exchange và quy tắc (còn gọi là ràng buộc hay binding).
[![Exchange](https://gpcoder.com/wp-content/uploads/2020/04/rabbitmq-exchange-types-1024x625.png?v=1589390967)]()

_Có 4 loại Exchange_: Direct, Fanout, Topic, Headers. Lựa chọn các exchange type khác nhau sẽ dẫn đến các đối xử khác nhau của message broker với tin nhắn nhận được từ producer. Exchange được bind (liên kêt) đến một số Queue nhất định.<br><br>
`**Direct Exchange**`<br>
Direct Exchange (trao đổi trực tiếp) định tuyến message đến Queue dựa vào routing key. Thường được sử dụng cho việc định tuyến tin nhắn unicast-đơn hướng (mặc dù nó có thể sử dụng cho định tuyến multicast-đa hướng). Các bước định tuyến message:
- Một queue được ràng buộc với một direct exchange bởi một routing key K.
- Khi có một message mới với routing key R đến direct exchange. Message sẽ được chuyển tới queue đó nếu R=K.
Một Exchange không xác định tên (empty ttring), đây là loại Default Exchange, một dạng đặc biệt của là Direct Exchange. Default Exchange được liên kết ngầm định với mọi Queue với khóa định tuyến bằng với tên Queue.<br>
[![](https://gpcoder.com/wp-content/uploads/2020/04/rabbitmq-direct-exchange.png?v=1589390965)]()<br>

Direct Exchange hữu ích khi muốn phân biệt các thông báo được publish cho cùng một exchange bằng cách sử dụng một mã định danh chuỗi đơn giản.<br><br>

`**Fanout Exchange**`:<br>
Fanout exchange định tuyến message (copy message) tới tất cả queue mà nó được bind, với bất kể một routing key nào. Giả sử, nếu nó N queue được bind bởi một Fanout exchange, khi một message mới published, exchange sẽ định tuyến message đó tới tất cả N queues. Fanout exchange được sử dụng cho định tuyến message broadcast (quảng bá).
[![Exchange](https://gpcoder.com/wp-content/uploads/2020/04/rabbitmq-fanout-exchange.png?v=1589390964)]()<br>
Exchange Fanout hữu ích với trường hợp ta cần một dữ liệu được gửi tới nhiều ứng dụng khác nhau với cùng một message nhưng cách xử lý ở ứng dụng là khác nhau.<br><br>

`**Topic Exchange (Publish/Subscribe)**`:<br>
Topic exchange định tuyến message tới một hoặc nhiều queue dựa trên sự trùng khớp giữa routing key và pattern. Topic exchange thường sử dụng để thực hiện định tuyến thông điệp multicast. Ví dụ một vài trường hợp sử dụng:
- Phân phối dữ liệu liên quan đến vị trí địa lý cụ thể.
- Xử lý tác vụ nền được thực hiện bởi nhiều workers, mỗi công việc có khả năng xử lý các nhóm tác vụ cụ thể.
- Cập nhật tin tức liên quan đến một category hoặc gắn tag.
- Điều phối các dịch vụ của các loại khác nhau trong cloud.<br>
  [![](https://gpcoder.com/wp-content/uploads/2020/04/rabbitmq-topic-exchange.png?v=1589390962)]()<br>
  
Một topic exchange sẽ sử dụng wildcard để gắn routing key với một routing pattern khai báo trong binding. Consumer có thể đăng ký những topic mà nó quan tâm.
Ví dụ:
- agreements.*.headstore : Được đăng ký bởi tất cả những key với pattern bắt đầu bằng agreements, theo sau là một từ bất kỳ và kết thúc là headstore.
- agreements.# : Được đăng ký bởi tất cả các key bắt đầu với agreements.<br><br>

`**Headers Exchange**`:<br>
Header exchange được thiết kế để định tuyến với nhiều thuộc tính, để dàng thực hiện dưới dạng header của message hơn là routing key. Header exchange bỏ đi routing key mà thay vào đó định tuyến dựa trên header của message. Trường hợp này, broker cần một hoặc nhiều thông tin từ application developer, cụ thể là, nên quan tâm đến những tin nhắn với tiêu đề nào phù hợp hoặc tất cả chúng.
Headers Exchange rất giống với Topic Exchange, nhưng nó định tuyến dựa trên các giá trị header thay vì routing key.
Một Message được coi là phù hợp nếu giá trị của header bằng với giá trị được chỉ định khi ràng buộc.<br>
[![](https://gpcoder.com/wp-content/uploads/2020/04/rabbitmq-headers-exchange.png?v=1589390963)]()<br><br>

`**Dead Letter Exchange**`<br>
Nếu không tìm thấy hàng đợi phù hợp cho tin nhắn, tin nhắn sẽ tự động bị hủy. RabbitMQ cung cấp một tiện ích mở rộng AMQP được gọi là “Dead Letter Exchange”. Cung cấp chức năng để chụp các tin nhắn không thể gửi được.
