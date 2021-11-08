package FiveStage

import chisel3.core.ExplicitCompileOptions.Strict
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
  
    
    val ID  = Module(new InstructionDecode)
    val IF  = Module(new InstructionFetch)
    val EX  = Module(new Execute)
    val MEM = Module(new MemoryFetch)
    
    val EXR = Module(new EXRegs)
    
    /**
        TODO: Your code here
    */
    
    ID.io.in.ins:=IF.io.ins
    ID.io.wdata:=EX.io.out.alu_data
    ID.io.waddr:=EX.io.out.rd
    ID.io.in.w_rd:=EX.io.out.w_rd
    
    EXR.io.out<>EX.io.in
    EXR.io.in<>ID.io.out
    
    
    
    
    val dontcare=Wire(UInt(32.W))
    dontcare:=DontCare
    
    
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
