<template>
  <div>
    <v-card class="pl-5 pr-5">
      <h1>Users</h1>
      <v-divider></v-divider>
      <div class="row" v-show="!isEdit">
        <div class="pl-3 col">
          <v-text-field
            v-model="Search"
            :error-messages="errors"
            label="Search"
            required
          ></v-text-field>
        </div>
        <div class="col">
          <div class="float-right">
            <v-btn color="primary" @click="onNew()"> New User </v-btn>
          </div>
        </div>
      </div>
      <div v-show="isEdit">
        <div class="row">
          <div class="col">
            <v-text-field
              v-model="name"
              :error-messages="nameErrors"
              label="Name"
              required
              @input="$v.name.$touch()"
              @blur="$v.name.$touch()"
            ></v-text-field>
          </div>
          <div class="col">
            <v-text-field
              v-model="Email"
              :error-messages="nameErrors"
              label="Email"
              required
              @input="$v.Email.$touch()"
              @blur="$v.Email.$touch()"
            ></v-text-field>
          </div>
        </div>
        <div class="row">
          <div class="col">
            <v-text-field
              v-model="PassphraseHint"
              :error-messages="nameErrors"
              label="Passphrase Hint"
              required
              @input="$v.PassphraseHint.$touch()"
              @blur="$v.PassphraseHint.$touch()"
            ></v-text-field>
          </div>
          <div class="col">
            <v-text-field
              v-model="Passphrase"
              :error-messages="nameErrors"
              label="Passphrase"
              required
              @input="$v.Passphrase.$touch()"
              @blur="$v.Email.$touch()"
            ></v-text-field>
          </div>
        </div>
        <div class="row">
          <div class="col">
            <div class="float-right">
              <v-btn @click="onCancel()"> Cancel </v-btn>
              <v-btn class="mr-4" color="primary" @click="onSave()">
                Save
              </v-btn>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col">
          <v-simple-table>
            <template v-slot:default>
              <thead>
                <tr>
                  <th class="text-left">Name</th>
                  <th class="text-left">Email</th>
                  <th style="width: 100px"></th>
                </tr>
              </thead>
              <tbody>
                <!-- <tr
          v-for="item in desserts"
          :key="item.name"
        >
          <td>{{ item.name }}</td>
          <td>{{ item.calories }}</td>
        </tr> -->
                <tr>
                  <td>name1</td>
                  <td>email1</td>
                  <td>
                    <v-icon class="mr-2" @click="onEdit()"
                      >mdi-comment-edit</v-icon
                    >
                    <v-icon class="mr-2" @click="onDelete()">mdi-delete</v-icon>
                  </td>
                </tr>
                <tr>
                  <td>name2</td>
                  <td>email2</td>
                  <td>
                    <v-icon class="mr-2">mdi-comment-edit</v-icon>
                    <v-icon class="mr-2">mdi-delete</v-icon>
                  </td>
                </tr>
              </tbody>
            </template>
          </v-simple-table>
        </div>
      </div>
    </v-card>
  </div>
</template>

<script>
export default {
  name: 'User',
  created() {
    this.vars = {

    };
  },
  methods: {
    onNew() {
      this.isEdit = true;
    },
    onSave() {
      this.isEdit = false;
    },
    onEdit() {
      this.isEdit = true;
    },
    onDelete() {
      this.isEdit = false;
    },
    onCancel() {
      this.isEdit = false;
    },
  },
  data() {
    return {
      isEdit: false,
      isNew: false,
    };
  },
};
</script>
<style scoped>
</style>
