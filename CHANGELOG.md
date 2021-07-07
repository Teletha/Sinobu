# Changelog

### [2.3.1](https://www.github.com/Teletha/sinobu/compare/v2.3.0...v2.3.1) (2021-07-04)


### Bug Fixes

* XML can escape '&' correctly. ([6104912](https://www.github.com/Teletha/sinobu/commit/61049123e7624bd494cebe4999e90c0415e260ea))
* XML#attr and #tagName returns emptry string if the specified ([327d76d](https://www.github.com/Teletha/sinobu/commit/327d76d382b62f544d0eabce1167dc74759ab022))

## [2.3.0](https://www.github.com/Teletha/sinobu/compare/v2.2.2...v2.3.0) (2021-04-18)


### Features

* I#express accepts wildcard [*]. ([d50d9b3](https://www.github.com/Teletha/sinobu/commit/d50d9b30101060f2cd09a78bb339a926729bfa7d))
* I#express can accept mustache-like section. ([440d73f](https://www.github.com/Teletha/sinobu/commit/440d73f03f41478c15eac1377d9833a7d31a151d))
* I#express supports comment section. ([fe0be3e](https://www.github.com/Teletha/sinobu/commit/fe0be3eabe8da3b6eb88baf97191ca03922666b2))
* I#express supports line based block. ([30214a7](https://www.github.com/Teletha/sinobu/commit/30214a728b42427f7b074a849e9e7010e944fbea))


### Bug Fixes

* Delay is too short. ([6d351cb](https://www.github.com/Teletha/sinobu/commit/6d351cbc936b263e4f61f9596d24b8b3d2f3a7d4))
* I#express can accept the nested section. ([0dcbadd](https://www.github.com/Teletha/sinobu/commit/0dcbadde269af4ad7f1579b770809285e458fd40))
* I#express can accepts "this" keyword. ([f10d2d6](https://www.github.com/Teletha/sinobu/commit/f10d2d6bceb483f28f2b83a664d9dad8682d093c))
* Optimize RegEx pattern. ([c3d843c](https://www.github.com/Teletha/sinobu/commit/c3d843c6fd9b098c8bb88b17fa29bbea7d8d7c19))

### [2.2.2](https://www.github.com/Teletha/sinobu/compare/v2.2.1...v2.2.2) (2021-03-28)


### Bug Fixes

* Ignore ClassNotFoundException during classpath scanning. ([27fd7b1](https://www.github.com/Teletha/sinobu/commit/27fd7b1b265a2727209ddd8a970a1049b2bbb640))

### [2.2.1](https://www.github.com/Teletha/sinobu/compare/v2.2.0...v2.2.1) (2021-03-25)


### Bug Fixes

* Delay is too short. ([dc875e9](https://www.github.com/Teletha/sinobu/commit/dc875e98259dfb7cf4030285d68aae7690ecc4ec))
* Delay is too short. ([d29a6a4](https://www.github.com/Teletha/sinobu/commit/d29a6a4603b64ab4f904f83c7ca5d41f0c207418))
* Make codes compilable by javac. ([ffb7e9b](https://www.github.com/Teletha/sinobu/commit/ffb7e9b7141594ead92b5dead911a47bd36f61ef))
* Signal#flatArray is invalid signature. ([8766bb4](https://www.github.com/Teletha/sinobu/commit/8766bb40f8778e073e329ff9c4bdfa1d82bbf332))

## 2.2.0 (2021-03-21)


### Bug Fixes

* Can't resolve outside interface type. ([4aaa403](https://www.github.com/Teletha/sinobu/commit/4aaa403a9ec45434ffc89f522fef2266afe90537))
* Class codec can't resolve primitive types. ([6e5e794](https://www.github.com/Teletha/sinobu/commit/6e5e7949c931ad098f2ba97d487830b3ec49baf6))
* Class loading in jar file is failed. ([bf97ea3](https://www.github.com/Teletha/sinobu/commit/bf97ea3675f352ec7f7058801fca720fc4adff12))
* ClassCodec can't decode array-type class. ([21aa7fa](https://www.github.com/Teletha/sinobu/commit/21aa7fa2e27d4b307671a071f08e95c79b451456))
* ClassCodec can't decode array-type class. ([418d834](https://www.github.com/Teletha/sinobu/commit/418d834ae08dd37cc9715150f612983907a0e85a))
* ClassUtil#getAnnotations collects non-override method's annotations if parent class has same signature private method. ([fe23eda](https://www.github.com/Teletha/sinobu/commit/fe23eda368066bf0dd07cbba72cb00d0d9898c96))
* ClassUtil#getAnnotations contains duplicate annotation. ([fe23eda](https://www.github.com/Teletha/sinobu/commit/fe23eda368066bf0dd07cbba72cb00d0d9898c96))
* ClassUtil#getParameter doesn't compute the correct class agains the overlapped parameter. ([bf78bd8](https://www.github.com/Teletha/sinobu/commit/bf78bd8bd585595236d89bfb8a91ac05782773ac))
* CleanRoom can't create file in not-existing directory. ([cbd0aa4](https://www.github.com/Teletha/sinobu/commit/cbd0aa45f0dde1a81f893198aa8420382905a24e))
* Codec for Locale doesn't use shared instance. ([a45e8f4](https://www.github.com/Teletha/sinobu/commit/a45e8f4ccd61277eeecddc6fa571435356914bec))
* Collection assisted Signal can't dispose. ([b968c71](https://www.github.com/Teletha/sinobu/commit/b968c71857e1cba52db290f155de2a603696a753))
* Crazy HTML crush application. (case sensitive related) ([3330fa1](https://www.github.com/Teletha/sinobu/commit/3330fa1946598a0733c11d266a857ae574b4628b))
* Disposed signal never emit any message. ([6a6332c](https://www.github.com/Teletha/sinobu/commit/6a6332c0f13e9b7e5787a733a8b8b7b0a7d79c7b))
* Doesn't recognize multiple escaped characters. ([097ce0d](https://www.github.com/Teletha/sinobu/commit/097ce0d4aa82d81dca2f007b370f2ee28e4d0e0f))
* End tail whitespace crush application. ([f69909b](https://www.github.com/Teletha/sinobu/commit/f69909baf8d969f73c4172d8cf16cc3c6b287b0f))
* Enum codec can't encode value if it's toString method is ([5605074](https://www.github.com/Teletha/sinobu/commit/5605074569a4ce4edd4c910360f3ef78328c3e12))
* Enum property ignores null value. ([fe48321](https://www.github.com/Teletha/sinobu/commit/fe483219aad89cabdf0b3eb403906a319b3db853))
* Events.NEVER is invalid. ([f6184fe](https://www.github.com/Teletha/sinobu/commit/f6184fecd8479487f81a06f9d9fff8f4a0aa42e2))
* Events#buffer(time, unit) should have side-effect-free updater. ([519f662](https://www.github.com/Teletha/sinobu/commit/519f6622fa2293ce47c630fe6b8db90e09d3d157))
* Events#to(Consumer) is not found. ([be9eca3](https://www.github.com/Teletha/sinobu/commit/be9eca37bae1302fe18247f3523cd0cc041113fe))
* Extension depends on the order of registration. ([9e00e69](https://www.github.com/Teletha/sinobu/commit/9e00e69e37dc6238d9db7ff3bf459eaacd4e490a))
* File observer system doesn't create deamon thred. ([ab24f44](https://www.github.com/Teletha/sinobu/commit/ab24f44dbbc8957946dd913872461441ec0fa4b7))
* File observer system doesn't handle directory exclude pattern properly. ([ab24f44](https://www.github.com/Teletha/sinobu/commit/ab24f44dbbc8957946dd913872461441ec0fa4b7))
* Guaranteed to execute the Signal#delay's complete event last. ([de76f82](https://www.github.com/Teletha/sinobu/commit/de76f82d784f8c00fc2c3fccb4ed7aaf5d536916))
* HttpRequestBuilder doesn't build HttpRequest. ([8d7b423](https://www.github.com/Teletha/sinobu/commit/8d7b423509b4e29967da46361fee58390228d18e))
* I#find may conflict hash. ([dba08a8](https://www.github.com/Teletha/sinobu/commit/dba08a81cb24a32743deaa58bb2e184f3e9ebc1d))
* I#json doesn't accept any Reader input. ([238fdd2](https://www.github.com/Teletha/sinobu/commit/238fdd220c440c76d687ed074a3a8c30a9c59f77))
* I#locate can't resolve escaped character. ([7b16b2b](https://www.github.com/Teletha/sinobu/commit/7b16b2b5e78d584e6c3dfd4404e603bae74e2afb))
* I#locate can't resolve file prorocol. ([f4cd980](https://www.github.com/Teletha/sinobu/commit/f4cd980886698753eb1dfa1d3d5771e30fb2f37f))
* I#make(Class) can't accept interface which has the external-provided Lifestyle. ([99ceb4c](https://www.github.com/Teletha/sinobu/commit/99ceb4c794a9b9b7b4a1e36cfc7b5ef1ca4f69ed))
* I#observe and I#bind throw NPE because they doesn't wipe thier context resources. ([9470a02](https://www.github.com/Teletha/sinobu/commit/9470a02f303cd0161202e2010d9c329f29cda81e))
* I#observe can't apply muliple times. ([7bbdad2](https://www.github.com/Teletha/sinobu/commit/7bbdad2388e5043edcb2d27a21a6eca8d1c1212e))
* I#read must not read transient property from json date. ([5a9ee56](https://www.github.com/Teletha/sinobu/commit/5a9ee5694aadb1dbb3f856cbc809bc06bae8cc85))
* I#walk can't resolve an archive file. ([9d7ac27](https://www.github.com/Teletha/sinobu/commit/9d7ac27dd9dd0eb278fba5394ebb423179b1fd75))
* I#walkDirectory can't recgnize patterns. ([8a36bba](https://www.github.com/Teletha/sinobu/commit/8a36bba95f94e1a562f80dcbf61c1336a29bbb8c))
* I#write method creates file automatically if needed. ([498dc88](https://www.github.com/Teletha/sinobu/commit/498dc88f1c114d5a0bf17e61c7c8d05e1f52612b))
* Ignore empty data on websocket binary. ([444bea0](https://www.github.com/Teletha/sinobu/commit/444bea025b917d666761b93a64c662a57f800837))
* Internal disposer in Events#flatMap affects external events. ([f1e0e6d](https://www.github.com/Teletha/sinobu/commit/f1e0e6d10e5b36db269a3461bf25b6a0299c95a0))
* Invalid encoding name crush application. ([8e6a249](https://www.github.com/Teletha/sinobu/commit/8e6a2492f47f238b9dd466b1c42f5e9995788545))
* Jar entry file name is invalid. ([8173f44](https://www.github.com/Teletha/sinobu/commit/8173f447fd12f63fdbe3b5cd4936c70014b93880))
* Javadoc is missing. ([3b103c1](https://www.github.com/Teletha/sinobu/commit/3b103c131da4b2f6bea943a865e4e8d234a4ea81))
* JSON serializer can handle nulls more properly. ([a73603d](https://www.github.com/Teletha/sinobu/commit/a73603daaf0d778c4b00ff18ddcdc90bbd12fde1))
* Junit has test scope. ([9bfe02c](https://www.github.com/Teletha/sinobu/commit/9bfe02c6798d6a7789e709f42bb0f6884284c573))
* Model accessor throws NPE when some parameter is null. ([07af68e](https://www.github.com/Teletha/sinobu/commit/07af68e4b126bf552f2102b7f5a84ca3a8b1955d))
* Model can't access field property in non-public class. ([6fc8db3](https://www.github.com/Teletha/sinobu/commit/6fc8db3a6399564d841b78ad977ec2f74e39ff0d))
* Model can't resolve the specialized generic type on Variable. ([0595db6](https://www.github.com/Teletha/sinobu/commit/0595db61df2c986467ac14bf9ba6ac0e199361a4))
* Model is not thread-safe. ([6646d5b](https://www.github.com/Teletha/sinobu/commit/6646d5b308a3835d86b7245b27b9daf18afc88da))
* ModelTest fails by class loading order. ([8a6f980](https://www.github.com/Teletha/sinobu/commit/8a6f98043d39e4740f854f5bc4057545f74a15c2))
* Multiple charset detection causes stack over flow. ([ac65de4](https://www.github.com/Teletha/sinobu/commit/ac65de4e5a0a5543cd3dc2029a3d37d6857dae25))
* Path decoder is not found. ([63165f7](https://www.github.com/Teletha/sinobu/commit/63165f7d909738347dbdd6292dcc93dc0566f891))
* PathObserver scan all decendant paths with direct child pattern. ([a673839](https://www.github.com/Teletha/sinobu/commit/a6738398b48947e30a7d8ac60e3dc832efeb3bd2))
* Property inspection is broken because I#recurse is async. ([aaec73f](https://www.github.com/Teletha/sinobu/commit/aaec73fc0b230a47cee1815403abd2b5e4b20202))
* ReusableRule burkes errors in test method. ([048ea2a](https://www.github.com/Teletha/sinobu/commit/048ea2aa9b48acaa604e6c68f84f7257654e12fc))
* ReusableRule throws NPE. ([7229b3c](https://www.github.com/Teletha/sinobu/commit/7229b3c5c3006f6872dfb7c7a8af22e79004cde3))
* SandBox throws IndexOutOfBoundsException when PATH environment value contains sequencial separator character. ([c4a1511](https://www.github.com/Teletha/sinobu/commit/c4a1511ead59a9de81de6762dc69504e94a75fb4))
* Scheduler must have positive core pool. ([add714e](https://www.github.com/Teletha/sinobu/commit/add714e952798c5ac5228ebe45dbce7893d03627))
* Signal error and complete disposes subscription. ([fdd3311](https://www.github.com/Teletha/sinobu/commit/fdd3311dfab2b2e2e28dde3937c435ba118d6016))
* Signal#combine completes immediately if the queue is empty. ([2ceb2f4](https://www.github.com/Teletha/sinobu/commit/2ceb2f4a37d099f2fa4ad814a5c668fe780c759a))
* Signal#delay delays complete event also. ([ad2c327](https://www.github.com/Teletha/sinobu/commit/ad2c3276ee73ad99bdfe42fb9868f91e0db75851))
* Signal#delay failed when complete event without any values. ([0f5db77](https://www.github.com/Teletha/sinobu/commit/0f5db77b597c3d7fbd7c748095bf932fe9ba9505))
* Signal#first disposes the following signal. ([0519471](https://www.github.com/Teletha/sinobu/commit/05194713b61094e6a4207aabd78e3cd7eca7be99))
* Signal#flatMap should ignore complete event from sup process. ([f4101b2](https://www.github.com/Teletha/sinobu/commit/f4101b215369d6001a5858d53807e5a42727b911))
* Signal#infinite can't dispose. ([325ed04](https://www.github.com/Teletha/sinobu/commit/325ed04d6abc02d631c275b428680c3ca173ba13))
* Signal#repeat is broken. ([e40e05b](https://www.github.com/Teletha/sinobu/commit/e40e05b69c6ef9858b83d7aedcd14cd2b9beeeb5))
* Signal#repeatWhen and #retryWhen may throw StackOverflowException. ([fd57182](https://www.github.com/Teletha/sinobu/commit/fd57182a6774f6fb94dc8a3c274a25bc2697697f))
* Signal#share disposes well. ([cf1ee62](https://www.github.com/Teletha/sinobu/commit/cf1ee624ed71eff21e8aee1285976ac2448e1fc5))
* Signal#signal related methods sometimes send COMPLETE event ([e616dfd](https://www.github.com/Teletha/sinobu/commit/e616dfd2e89e55b3ba18576c22437d965409ce8f))
* Signal#startWith(Signal) can't return root disposer. ([5948e2f](https://www.github.com/Teletha/sinobu/commit/5948e2fd24bd9487f8f15e7c7b6b69d2b35ba797))
* Signal#startWith(Supplier) is lazily called. ([ca47a6e](https://www.github.com/Teletha/sinobu/commit/ca47a6e1d8258ca0f9265af372c5bdfbe4be3fae))
* Signal#take and #skip related methods sometimes send COMPLETE event ([c132efd](https://www.github.com/Teletha/sinobu/commit/c132efd8819b273b292c312462e55e1584c52b4a))
* Signal#takeWhile can't drop unconditional data. ([8f69f4e](https://www.github.com/Teletha/sinobu/commit/8f69f4e833c58edeef06ba6d7714e9313b32f800))
* Signale#combine awaits all completions. ([4aecb47](https://www.github.com/Teletha/sinobu/commit/4aecb47e9c8e1a4144fea24e555650157e581a74))
* Sinobu writes invalid JSON format. ([4d6dce3](https://www.github.com/Teletha/sinobu/commit/4d6dce3089ff7c5de168e02fc506f29a2d825918))
* Test for archive. ([c6d7d28](https://www.github.com/Teletha/sinobu/commit/c6d7d285190bd8d940bfd920ca5a8d9513a9084d))
* TestSuite brokes some tests. ([080d806](https://www.github.com/Teletha/sinobu/commit/080d80613e20d19d2f3edab08016a2afbf733ffc))
* The glob pattern "**" ignores other patterns. ([0a53ded](https://www.github.com/Teletha/sinobu/commit/0a53ded8c447943d23a69a0ec36bfa3db70fa803))
* The validator must not call the duration supplier. ([1002d68](https://www.github.com/Teletha/sinobu/commit/1002d68b80cd81ac39ac0c65d5d9b4a92bc3dfd8))
* Transient property on Variable field is ignored. ([d00a7cc](https://www.github.com/Teletha/sinobu/commit/d00a7cc44fb45f9dbf0b672de650aac6d82e25c0))
* TypeVariable must use not "==" operator but "equals" method to check equality. ([bca5acd](https://www.github.com/Teletha/sinobu/commit/bca5acdfb4ec9b6d538054d8dc45226d826d7277))
* URI encodes automatically. ([002849b](https://www.github.com/Teletha/sinobu/commit/002849b4ad42bd66de3095c8f781c03f0da37e53))
* URL class try to access external resource at test phase. ([4622de9](https://www.github.com/Teletha/sinobu/commit/4622de9adaf6f8f0db42c5c1110c849f09b88efa))
* URLConnection requres some user-agent property. ([1b69ede](https://www.github.com/Teletha/sinobu/commit/1b69ede4c2d4c7323940690de7a5f166f1b055ec))
* Visitor can't accept glob pattern. ([545e0f8](https://www.github.com/Teletha/sinobu/commit/545e0f88947b4b42cdc39131a89145d518b6d082))
* Websocket can't handle long-size binary. ([e274300](https://www.github.com/Teletha/sinobu/commit/e2743003534b6157c5e713d352fd99335f0e6d7f))
* When a object has empty list, JSON writes invalid format. ([a0e7e1a](https://www.github.com/Teletha/sinobu/commit/a0e7e1aaea86197bc218b2caff402ec851105047))
* WiseTriConsumer must throw error. ([4fc8907](https://www.github.com/Teletha/sinobu/commit/4fc8907a9113175884af52fa39d6cdd6de94edf7))
* Wrong HTML crush application. (attribute related) ([071f6db](https://www.github.com/Teletha/sinobu/commit/071f6db79e39f32552274a4aacffacf7b44d0b14))
* XML and JSON serialization can't handle escaped linefeed characeter. ([4ef2239](https://www.github.com/Teletha/sinobu/commit/4ef2239d27f2a7b761720e0f85fa480c20efeae8))
* XML can't parse document which has text node in root directly. ([ef49f42](https://www.github.com/Teletha/sinobu/commit/ef49f42039c1858a7375eba40a948c8f887dafbb))
* XML confuses xml like text. ([3cee035](https://www.github.com/Teletha/sinobu/commit/3cee035cc2bd5bf731fa4fad1790ba4c2d06da73))
* XML#last and #first rise error if they are empty. ([dd1ea06](https://www.github.com/Teletha/sinobu/commit/dd1ea06f96ba736584270de36b6a356ff9cdcf34))
* XML#to doesn't flush data properly. ([64d717b](https://www.github.com/Teletha/sinobu/commit/64d717b0193ae787ec2444643eb97d3467c9cd2e))
* XMLScanner can't use extended rule method. ([61bac76](https://www.github.com/Teletha/sinobu/commit/61bac76f8af06b212f9f65b6cc5acbb8c567399d))
* XMLScanner rises StackOverFlowError properly in invalid  method call. ([afab5cc](https://www.github.com/Teletha/sinobu/commit/afab5cca937d9711e5ffd08aeb4369dac4686758))
* XMLWriter outputs invalid CDATA. ([2c84e8e](https://www.github.com/Teletha/sinobu/commit/2c84e8eb3dcfc03c5f9942e6fa11b8958aa7c423))
