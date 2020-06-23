<template>
  <el-container>
    <el-main>
      <el-tabs type="card">
        <el-tab-pane label="Personal">
          <el-table :data="personal" split style="width: 100%; font-size: 12px;" max-height="350" >
            <el-table-column sortable label="Primer Apellido" prop="primer_apellido" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.primer_apellido }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Segundo Apellido" prop="segundo_apellido" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.segundo_apellido }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Saldo" prop="Nombre" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.nombre }}</span>
              </template>
            </el-table-column>
            <el-table-column>
              <template slot-scope="scope">
                <el-button type="primary" icon="el-icon-service" size="mini" circle title="Dirección y Teléfono" @click="buscarDireccion(scope.row)"/>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="Hipotecaria">
          <el-table :data="real" split style="width: 100%; font-size: 12px;" max-height="350" >
            <el-table-column sortable label="No. Escritura" prop="numero_escritura" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.numero_escritura }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Fecha Escritura" prop="fecha_escritura" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_escritura | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Notaría" prop="notaria" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.notaria }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Ciudad" prop="ciudad_escritura" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.ciudad_escritura }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Matrícula" prop="matricula" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.matricula }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Fecha Matrícula" prop="fecha_matricula" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_matricula | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Cuenta Orden" prop="cuentas_de_orden" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">${{ scope.row.cuentas_de_orden | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Poliza de Incendio" prop="poliza_incendio" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.poliza_incendio }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Valor Asegurado" prop="valor_asegurado" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.valor_asegurado }}</span>
              </template>
            </el-table-column>
            <el-table-column label="F.I.Póliza" prop="fecha_inicial_poliza" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_inicial_poliza | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="F.F.Póliza" prop="fecha_final_poliza" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_final_poliza | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="Pignoración">
          <el-table :data="pignoracion" split style="width: 100%; font-size: 12px;" max-height="350" >
            <el-table-column sortable label="Placa" prop="numero_escritura" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.numero_escritura }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Fecha Radicación" prop="fecha_escritura" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_escritura }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Cuenta Orden" prop="cuentas_de_orden" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">${{ scope.row.cuentas_de_orden | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Poliza de Incendio" prop="poliza_incendio" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.poliza_incendio }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Valor Asegurado" prop="valor_asegurado" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.valor_asegurado }}</span>
              </template>
            </el-table-column>
            <el-table-column label="F.I.Póliza" prop="fecha_inicial_poliza" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_inicial_poliza | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="F.F.Póliza" prop="fecha_final_poliza" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_final_poliza | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <el-row>
        <el-col :span="24">
          <el-button type="primary" @click="Cerrar()">Cerrar</el-button>
        </el-col>
      </el-row>
    </el-main>
    <el-dialog :visible.sync="showDireccionDlg" :show-close="false" :destroy-on-close="true" :title=" 'Dirección y Teléfono de ' + nombrecompleto" width="90%" append-to-body>
      <direccion :direcciones="direcciones" @cerrarDireccionEvent="cerrarDireccionEvento"/>
    </el-dialog>
  </el-container>
</template>
<script>
import DireccionPersonaComponent from '@/components/DireccionPersona'
import { buscarDireccionPersona } from '@/api/controlcobro'
export default {
  name: 'GarantiaCreditoComponent',
  components: {
    'direccion': DireccionPersonaComponent
  },
  props: {
    colocacion: {
      type: String,
      required: true
    },
    personal: {
      type: Array,
      required: true
    },
    real: {
      type: Array,
      required: true
    },
    pignoracion: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      showDireccionDlg: false,
      nombrecompleto: ''
    }
  },
  methods: {
    Cerrar() {
      this.$emit('cerrarGarantiaEvent')
    },
    cerrarDireccionEvento() {
      this.direcciones = []
      this.showDireccionDlg = false
    },
    buscarDireccion(persona) {
      this.nombrecompleto = persona.nombre + ' ' + persona.primer_apellido + ' ' + persona.segundo_apellido
      this.direcciones = []
      buscarDireccionPersona(persona.id_identificacion, persona.id_persona).then(response => {
        this.direcciones = response.data
        this.showDireccionDlg = true
      }).catch(() => {
        this.showDireccionDlg = false
      })
    }
  }
}
</script>
