package FiveStage
import chisel3._
import chisel3.experimental.MultiIOModule

class InstructionFetch extends MultiIOModule {

  // Don't touch
  val testHarness = IO(
    new Bundle {
      val IMEMsetup = Input(new IMEMsetupSignals)
      val PC        = Output(UInt())
    }
  )


  /**
    * TODO: Add input signals for handling events such as jumps

    * TODO: Add output signal for the instruction. 
    * The instruction is of type Bundle, which means that you must
    * use the same syntax used in the testHarness for IMEM setup signals
    * further up.
    */
  val io = IO(
    new Bundle {
      val ins_addr = Output(UInt())
      val ins= Output(new Instruction) 
    })

  val IMEM = Module(new IMEM)
  val ins_addr = RegInit(UInt(32.W), 0.U)


  /**
    * Setup. You should not change this code
    */
  IMEM.testHarness.setupSignals := testHarness.IMEMsetup
  testHarness.PC := IMEM.testHarness.requestedAddress


  /**
    * TODO: Your code here.
    * 
    * You should expand on or rewrite the code below.
    */
  io.ins_addr := ins_addr
  IMEM.io.instructionAddress := ins_addr

  ins_addr := ins_addr + 4.U

  val ins = Wire(new Instruction)
  ins := IMEM.io.instruction.asTypeOf(new Instruction)
  io.ins:=ins


  /**
    * Setup. You should not change this code.
    */
  when(testHarness.IMEMsetup.setup) {
    ins_addr := 0.U
    ins := Instruction.NOP
  }
}
