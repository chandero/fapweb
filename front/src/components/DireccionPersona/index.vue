<template>
  <el-container>
    <el-main>
      <el-table :data="direcciones" style="width: 100%; font-size: 11px;">
        <el-table-column label="Tipo" width="150">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ tipodireccion(scope.row.id_direccion) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Dirección" width="250">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.direccion | fm_truncate(250) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Barrio" width="150">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.barrio }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Municipio" width="150">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ municipio(scope.row.cod_municipio) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="Telefonos" width="400">
          <el-table-column width="100">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.telefono1 }}</span>
            </template>
          </el-table-column>
          <el-table-column width="100">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.telefono2 }}</span>
            </template>
          </el-table-column>
          <el-table-column width="100">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.telefono3 }}</span>
            </template>
          </el-table-column>
          <el-table-column width="100">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.telefono4 }}</span>
            </template>
          </el-table-column>
        </el-table-column>
      </el-table>
      <el-row>
        <el-col :span="24">
          <el-button type="primary" @click="Cerrar()">Cerrar</el-button>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { obtenerListaTipoDireccion, obtenerListaMunicipios } from '@/api/tipos'

export default {
  name: 'DireccionPersonaComponent',
  props: {
    direcciones: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      tiposdireccion: [],
      municipios: []
    }
  },
  mounted() {
    obtenerListaTipoDireccion().then(response => {
      this.tiposdireccion = response.data
      obtenerListaMunicipios().then(response => {
        this.municipios = response.data
      }).catch(error => {
        console.log('Error Consultando Lista Municipios: ' + error)
      })
    }).catch(error => {
      console.log('Error Consultando Lista Tipo Direccion: ' + error)
    })
  },
  methods: {
    Cerrar() {
      this.$emit('cerrarDireccionEvent')
    },
    tipodireccion(id) {
      if (this.tiposdireccion.length > 0) {
        if (id === undefined || id === null) {
          return ''
        } else {
          return this.tiposdireccion.find(o => o.id === id, { descripcion: '' }).descripcion
        }
      }
    },
    municipio(id) {
      if (this.municipios.length > 0) {
        if (id === undefined || id === null) {
          return ''
        } else {
          return this.municipios.find(o => o.cod_municipio === id, { nombre: '' }).nombre
        }
      }
    }
  }
}
</script>
