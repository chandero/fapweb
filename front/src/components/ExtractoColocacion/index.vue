<template>
  <el-container>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-table :data="datos" :expand-change="buscarDetalle()" stripe style="width: 100%; font-size: 12px;" max-height="300">
            <el-table-column type="expand">
              <template slot-scope="detalle">
                <el-table :data="detalle.row.datos">
                  <el-table-column label="Código">
                    <template slot-scope="scope">
                      <span style="margin-left: 10px">{{ scope.row.codigo }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </template>
            </el-table-column>
            <el-table-column sortable label="Fecha" prop="fecha_extracto" width="110">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.fecha_extracto | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column sortable label="Nota" prop="id_cbte_colocacion" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.id_cbte_colocacion }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Cuota" prop="cuota_extracto" align="right" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.cuota_extracto }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Capital" prop="abono_capital" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.abono_capital | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="%Cobro" prop="tasa_interes_liquidacion" align="right" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.tasa_interes_liquidacion }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Interés" prop="abono_interes" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.abono_cxc + scope.row.abono_anticipado + scope.row.abono_servicios | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Saldo" prop="saldo_anterior_extracto" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.saldo_anterior_extracto - scope.row.abono_capital | currency }}</span>
              </template>
            </el-table-column>
            <el-table-column label="F.Vencimiento" prop="interes_pago_hasta" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.interes_pago_hasta | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="D. Mora" prop="dias_mora" align="right" width="80">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.dias_mora }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Cap. Hasta" prop="capital_pago_hasta" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.capital_pago_hasta | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="Int. Hasta" prop="interes_pago_hasta" align="right" width="150">
              <template slot-scope="scope">
                <span style="margin-left: 10px">{{ scope.row.interes_pago_hasta | moment('YYYY-MM-DD') }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-button type="primary" @click="Cerrar()">Cerrar</el-button>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>

import { obtenerExtractoColocacionDetallado } from '@/api/extractocolocacion'

export default {
  name: 'ExtractoColocacionComponent',
  props: {
    colocacion: {
      type: String,
      required: true
    },
    datos: {
      type: Array,
      required: true
    }
  },
  mounted() {
    console.log('Datos: ' + JSON.stringify(this.datos))
  },
  methods: {
    Cerrar() {
      this.$emit('cerrarExtractoEvent')
    },
    buscarDetalle(row, expanded) {
      console.log('evento expand fired: ' + JSON.stringify(row))
      console.log('expanded: ' + expanded)
      if (row) {
        obtenerExtractoColocacionDetallado(row.id_colocacion, row.id_cbte_colocacion, row.fecha_extracto).then(response => {
          row.datos = response.data
        })
      }
    }
  }
}
</script>
