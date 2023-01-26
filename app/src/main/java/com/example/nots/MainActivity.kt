package com.example.nots

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
//import android.widget.SearchView
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.nots.adapter.NotesAdapter
import com.example.nots.Database.NoteDatabase
import com.example.nots.Models.NoteViewModel
import com.example.nots.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NotesAdapter.NotesClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var database : NoteDatabase
    lateinit var viewModel : NoteViewModel
    lateinit var adapter : NotesAdapter
    lateinit var selectedNote : com.example.nots.Models.Note

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if(result.resultCode == Activity.RESULT_OK){

            val note = result.data?.getSerializableExtra("note") as com.example.nots.Models.Note

            if(note != null){

                viewModel.updateNote(note)

            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initializing the UI
        initUI()

        viewModel = ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NoteViewModel::class.java)


        viewModel.allnotes.observe(this) {list ->
            list?.let {
                adapter.updateList(list)
            }
        }

        database = NoteDatabase.getDatabase(this)
        binding
    }

    private fun initUI() {

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this, this)
        binding.recyclerView.adapter =  adapter

        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if(result.resultCode == Activity.RESULT_OK) {

                val note = result.data?.getSerializableExtra("note") as com.example.nots.Models.Note

                if(note != null){

                    viewModel.insertNote(note)

                }

            }
        }

        binding.fbAddNotes.setOnClickListener{

            val intent = Intent(this, AddNote::class.java)
            getContent.launch(intent)
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null) {
                    adapter.filterlist(newText)
                }

                return true
            }

        })



    }

    override fun onItemClicked(note: com.example.nots.Models.Note) {

        val intent = Intent(this@MainActivity, AddNote::class.java)
        intent.putExtra("current_note", note)

    }

    override fun onLongItemClicked(note: com.example.nots.Models.Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(this, cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.delete_note) {
            viewModel.deleteNote(selectedNote)
            return true
        }
        return false
    }


}
