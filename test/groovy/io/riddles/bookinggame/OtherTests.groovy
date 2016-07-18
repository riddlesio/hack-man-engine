//import io.riddles.javainterface.io.IOHandler
//
//def "test engine setup"() {
//    println("test engine setup")
//
//    given:
//    def engine = new TestEngine(Mock(IOHandler));
//    engine.getIOHandler().getNextMessage() >>> ["initialize", "bot_ids 1,2", "start"]
//
//    when:
//    engine.setup()
//
//    then:
//    1 * engine.getIOHandler().sendMessage("ok")
//
//    expect:
//    engine.getPlayers().size() == 2
//    engine.getPlayers().get(0).getId() == 1
//    engine.getPlayers().get(1).getId() == 2
//}
//
//def "test engine configuration"() {
//    println("test engine configuration")
//
//    setup:
//    String[] botInputs = new String[2]
//    def wrapperInput = "./test/wrapper_input.txt"
//    botInputs[0] = "./test/bot1_input.txt"
//    botInputs[1] = "./test/bot2_input.txt"
//
//    def engine = new TestEngine(wrapperInput, botInputs)
//
//    engine.run()
//
//    expect:
//    engine.getPlayers().size() == 2
//    engine.getPlayers().get(0).getId() == 1
//    engine.getPlayers().get(1).getId() == 2
//    engine.configuration.get("max_rounds") == 10
//    engine.configuration.get("weapon_paralysis_duration") == 2
//}
