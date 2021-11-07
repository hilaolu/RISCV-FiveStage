package FiveStage

// import chisel3.ExplicitCompileOptions.Strict
//todo avoid implicit truncation
import chisel3._
import chisel3.core.Input
import chisel3.experimental.MultiIOModule
import chisel3.experimental._


class CPU extends MultiIOModule {

    val testHarness = IO(
        new Bundle {
            val setupSignals = Input(new SetupSignals)
            val testReadouts = Output(new TestReadouts)
            val regUpdates   = Output(new RegisterUpdates)
            val memUpdates   = Output(new MemUpdates)
            val currentPC    = Output(UInt(32.W))
        }
    )
  
    /**
    You need to create the classes for these yourself
    */
    // val IFBarrier  = Module(new IFBarrier).io
    // val IDBarrier  = Module(new IDBarrier).io
    // val EXBarrier  = Module(new EXBarrier).io
    // val MEMBarrier = Module(new MEMBarrier).io
    
    val ID  = Module(new InstructionDecode)
    val IF  = Module(new InstructionFetch)
    val EX  = Module(new Execute)
    val MEM = Module(new MemoryFetch)
    
    /**
        TODO: Your code here
    */
    
    ID.io.ins:=IF.io.ins
    ID.io.wdata:=EX.io.wdata
    ID.io.waddr:=EX.io.waddr
    
    EX.io.op_0:=ID.io.op_0
    EX.io.op_1:=ID.io.op_1
    EX.io.alu_op:=ID.io.alu_op
    EX.io.dst:=ID.io.dst
    
    
    // val WB  = Module(new Execute) (You may not need this one?)
    
    {
        /**
        *   Setup. You should not change this code
        */
        IF.testHarness.IMEMsetup     := testHarness.setupSignals.IMEMsignals
        ID.testHarness.registerSetup := testHarness.setupSignals.registerSignals
        MEM.testHarness.DMEMsetup    := testHarness.setupSignals.DMEMsignals
        
        testHarness.testReadouts.registerRead := ID.testHarness.registerPeek
        testHarness.testReadouts.DMEMread     := MEM.testHarness.DMEMpeek
        
        /**
            spying stuff
        */
        testHarness.regUpdates := ID.testHarness.testUpdates
        testHarness.memUpdates := MEM.testHarness.testUpdates
        testHarness.currentPC  := IF.testHarness.PC
    }
    
    
    
}
