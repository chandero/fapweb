<template>
  <el-container>
    <el-main>
      <el-form>
        <el-row :gutter="4">
          <el-col :span="5">
            <el-form-item label="Primer Apellido">
              <el-input v-model="data.primer_apellido" @input="data.primer_apellido=data.primer_apellido.toUpperCase()"/>
            </el-form-item>
          </el-col>
          <el-col :span="1">
            <span>&nbsp;</span>
          </el-col>
          <el-col :span="5">
            <el-form-item label="Segundo Apellido">
              <el-input v-model="data.segundo_apellido" @input="data.segundo_apellido=data.segundo_apellido.toUpperCase()" />
            </el-form-item>
          </el-col>
          <el-col :span="1">
            <span>&nbsp;</span>
          </el-col>
          <el-col :span="5">
            <el-form-item label="Nombre">
              <el-input v-model="data.nombre" @input="data.nombre=data.nombre.toUpperCase()" />
            </el-form-item>
          </el-col>
          <el-col :span="5">
            <el-form-item>
              <el-button :disabled="data.primer_apellido == null && data.segundo_apellido == null && data.nombre == null" type="success" icon="el-icon-search" title="Buscar Datos" @click="buscarPorNombre()">Buscar</el-button>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col>
            <el-table v-loading="loading" :data="personas" stripe style="width: 100%; font-size: 12px;" max-height="350">
              <el-table-column sortable label="Tipo" prop="id_identificacion" width="80">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.id_identificacion }}</span>
                </template>
              </el-table-column>
              <el-table-column sortable label="Documento" prop="id_persona" width="150">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.id_persona }}</span>
                </template>
              </el-table-column>
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
              <el-table-column sortable label="Nombre" prop="nombre" width="250">
                <template slot-scope="scope">
                  <span style="margin-left: 10px">{{ scope.row.nombre }}</span>
                </template>
              </el-table-column>
              <el-table-column
                fixed="right"
                label="Seleccionar"
                width="100">
                <template slot-scope="scope">
                  <el-button type="primary" icon="el-icon-user" circle title="Seleccionar" @click="seleccionar(scope.row.id_identificacion, scope.row.id_persona)" />
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>
      </el-form>
    </el-main>
  </el-container>
</template>

<script>

import { obtenerPersonaPorApellidosYNombre } from '@/api/persona'

export default {
  name: 'BuscarPersonaComponent',
  props: {
    primer_apellido: {
      type: String,
      required: false,
      default: ''
    },
    segundo_apellido: {
      type: String,
      required: false,
      default: ''
    },
    nombre: {
      type: String,
      required: false,
      default: ''
    }
  },
  data() {
    return {
      personas: [],
      loading: null,
      data: {
        primer_apellido: null,
        segundo_apellido: null,
        nombre: null
      }
    }
  },
  beforeMount() {
    this.data.primer_apellido = this.primer_apellido
    this.data.segundo_apellido = this.segundo_apellido
    this.data.nombre = this.nombre
  },
  methods: {
    buscarPorNombre() {
      obtenerPersonaPorApellidosYNombre(this.data.primer_apellido.trim() + '%', this.data.segundo_apellido.trim() + '%', this.data.nombre.trim() + '%').then(response => {
        this.personas = response.data
      })
    },
    seleccionar(id_identificacion, id_persona) {
      this.primer_apellido = ''
      this.segundo_apellido = ''
      this.nombre = ''
      this.personas = []
      const data = {
        id_identificacion: id_identificacion,
        id_persona: id_persona
      }
      this.$emit('selectPersonEvent', data)
    }
  }
}
</script>
