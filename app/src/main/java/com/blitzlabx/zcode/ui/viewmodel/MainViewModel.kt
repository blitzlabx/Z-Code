package com.blitzlabx.zcode.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.blitzlabx.zcode.core.*
import com.blitzlabx.zcode.data.HistoryEntity
import com.blitzlabx.zcode.data.HistoryDao
import com.blitzlabx.zcode.data.SettingsRepository
import com.blitzlabx.zcode.data.ZCodeDatabase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val db: ZCodeDatabase,
    private val settings: SettingsRepository
) : ViewModel() {

    private val historyDao: HistoryDao = db.historyDao()

    val theme = settings.theme
    val onboardingDone = settings.onboardingDone
    val saveHistory = settings.saveHistory
    val autoCopy = settings.autoCopy

    val history: StateFlow<List<HistoryEntity>> = historyDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current working state
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    private val _zCodeResult = MutableStateFlow<ZCodeEngine.EncodeResult?>(null)
    val zCodeResult: StateFlow<ZCodeEngine.EncodeResult?> = _zCodeResult

    private val _decodeResult = MutableStateFlow<ZCodeEngine.DecodeResult?>(null)
    val decodeResult: StateFlow<ZCodeEngine.DecodeResult?> = _decodeResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _codeType = MutableStateFlow(CodeType.COMPACT)
    val codeType: StateFlow<CodeType> = _codeType

    private val _hash = MutableStateFlow(HashAlgorithm.SHA256)
    val hash: StateFlow<HashAlgorithm> = _hash

    private val _zLanguage = MutableStateFlow(ZLanguage.Z_ALPHA)
    val zLanguage: StateFlow<ZLanguage> = _zLanguage

    private val _mode = MutableStateFlow(Mode.STANDARD)
    val mode: StateFlow<Mode> = _mode

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _stats = MutableStateFlow(Triple(0, 0, 0)) // total, saved, history count
    val stats: StateFlow<Triple<Int, Int, Int>> = _stats

    init {
        viewModelScope.launch {
            settings.defaultCodeType.collect { _codeType.value = CodeType.fromId(it) }
        }
        viewModelScope.launch {
            settings.defaultHash.collect { _hash.value = HashAlgorithm.fromId(it) }
        }
        viewModelScope.launch {
            settings.defaultLanguage.collect { _zLanguage.value = ZLanguage.fromId(it) }
        }
        viewModelScope.launch {
            settings.defaultMode.collect { _mode.value = Mode.fromId(it) }
        }
        viewModelScope.launch {
            history.collect { list ->
                val total = list.size
                val saved = list.count { it.isSaved }
                _stats.value = Triple(total, saved, total)
            }
        }
    }

    fun setInputText(text: String) { _inputText.value = text }
    fun setCodeType(v: CodeType) { _codeType.value = v; viewModelScope.launch { settings.setDefaultCodeType(v.id) } }
    fun setHash(v: HashAlgorithm) { _hash.value = v; viewModelScope.launch { settings.setDefaultHash(v.id) } }
    fun setZLanguage(v: ZLanguage) { _zLanguage.value = v; viewModelScope.launch { settings.setDefaultLanguage(v.id) } }
    fun setMode(v: Mode) { _mode.value = v; viewModelScope.launch { settings.setDefaultMode(v.id) } }
    fun setPassword(v: String) { _password.value = v }
    fun clearError() { _error.value = null }
    fun clearResults() {
        _zCodeResult.value = null
        _decodeResult.value = null
        _error.value = null
    }

    fun encode() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val config = ZCodeConfig(
                    codeType = _codeType.value,
                    hash = _hash.value,
                    zLanguage = _zLanguage.value,
                    mode = _mode.value,
                    password = _password.value.ifBlank { null }
                )
                val result = ZCodeEngine.encode(_inputText.value, config)
                _zCodeResult.value = result
                if (settings.saveHistory.first()) {
                    historyDao.insert(
                        HistoryEntity(
                            type = "generate",
                            originalText = _inputText.value,
                            zCode = result.zCode,
                            codeType = config.codeType.id,
                            hash = config.hash.id,
                            zLanguage = config.zLanguage.id,
                            mode = config.mode.id,
                            hasPassword = config.password != null
                        )
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Encoding failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun decode() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val preferred = ZCodeConfig(
                    codeType = _codeType.value,
                    hash = _hash.value,
                    zLanguage = _zLanguage.value,
                    mode = _mode.value
                )
                val result = ZCodeEngine.decode(
                    _inputText.value,
                    password = _password.value.ifBlank { null },
                    preferredConfig = preferred
                )
                _decodeResult.value = result
                if (settings.saveHistory.first()) {
                    historyDao.insert(
                        HistoryEntity(
                            type = "translate",
                            originalText = result.text,
                            zCode = _inputText.value,
                            codeType = result.detectedConfig.codeType.id,
                            hash = result.detectedConfig.hash.id,
                            zLanguage = result.detectedConfig.zLanguage.id,
                            mode = result.detectedConfig.mode.id,
                            hasPassword = false
                        )
                    )
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Decoding failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setOnboardingDone(done: Boolean) {
        viewModelScope.launch { settings.setOnboardingDone(done) }
    }

    fun clearHistory() {
        viewModelScope.launch { historyDao.clearAll() }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch { historyDao.delete(id) }
    }

    fun toggleSaved(id: Long, saved: Boolean) {
        viewModelScope.launch { historyDao.setSaved(id, saved) }
    }

    // Settings passthrough
    val autoDetect = settings.autoDetect
    val preserveExact = settings.preserveExact
    val confirmDestructive = settings.confirmDestructive
    val requirePasswordSecure = settings.requirePasswordSecure
    val clearPasswordAfter = settings.clearPasswordAfter

    fun setTheme(v: String) = viewModelScope.launch { settings.setTheme(v) }
    fun setAutoDetect(v: Boolean) = viewModelScope.launch { settings.setAutoDetect(v) }
    fun setPreserveExact(v: Boolean) = viewModelScope.launch { settings.setPreserveExact(v) }
    fun setAutoCopy(v: Boolean) = viewModelScope.launch { settings.setAutoCopy(v) }
    fun setSaveHistory(v: Boolean) = viewModelScope.launch { settings.setSaveHistory(v) }
    fun setConfirmDestructive(v: Boolean) = viewModelScope.launch { settings.setConfirmDestructive(v) }
    fun setRequirePasswordSecure(v: Boolean) = viewModelScope.launch { settings.setRequirePasswordSecure(v) }
    fun setClearPasswordAfter(v: Boolean) = viewModelScope.launch { settings.setClearPasswordAfter(v) }
}

class MainViewModelFactory(
    private val db: ZCodeDatabase,
    private val settings: SettingsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(db, settings) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}
