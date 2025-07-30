#include <jni.h>
#include <string>
#include <vector>
#include <unistd.h>
#include <android/log.h>
#include <sys/wait.h>

#define TAG "variables"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

constexpr const char *UEFI_CMD = "find /mnt/sdcard/UEFI/ -type f -name *.img";
constexpr int UEFI_TYPES[] = {60, 90, 120};

std::string executeRootCommand(const char *cmd) {
    int pipefd[2];
    if (pipe(pipefd) == -1) {
        return "";
    }

    pid_t pid = fork();
    if (pid == 0) {
        dup2(pipefd[1], STDOUT_FILENO);
        close(pipefd[0]);
        close(pipefd[1]);

        execlp("su", "su", "-c", cmd, (char *) nullptr);
        _exit(127);
    }

    close(pipefd[1]);

    std::string result;
    char buffer[256];
    ssize_t count;
    while ((count = read(pipefd[0], buffer, sizeof(buffer))) > 0) {
        result.append(buffer, count);
    }

    close(pipefd[0]);
    waitpid(pid, nullptr, 0);
    return result;
}

extern "C" {

JNIEXPORT jstring
JNICALL
Java_com_remtrik_m3khelper_util_VariablesKt_getPanelNative(
        JNIEnv *env,
        jobject jclass
) {
    std::string panel = executeRootCommand("cat /proc/cmdline");
    if (panel.empty()) {
        return env->NewStringUTF("Unknown");
    }

    std::transform(panel.begin(), panel.end(), panel.begin(), ::tolower);

    if (panel.find("samsung") != std::string::npos ||
        panel.find("ea8076") != std::string::npos ||
        panel.find("s6e3fc3") != std::string::npos ||
        panel.find("ams646yd01") != std::string::npos) {
        return env->NewStringUTF("Samsung");
    } else if (panel.find("j20s_42") != std::string::npos ||
               panel.find("k82_42") != std::string::npos ||
               panel.find("huaxing") != std::string::npos) {
        return env->NewStringUTF("Huaxing");
    } else if (panel.find("j20s_36") != std::string::npos ||
               panel.find("tianma") != std::string::npos ||
               panel.find("k82_36") != std::string::npos) {
        return env->NewStringUTF("Tianma");
    } else if (panel.find("ebbg") != std::string::npos) {
        return env->NewStringUTF("EBBG");
    }

    return env->NewStringUTF("Invalid");
}

JNIEXPORT jintArray
JNICALL
Java_com_remtrik_m3khelper_util_VariablesKt_findUEFIImages(
        JNIEnv *env,
        jobject jclass,
        jstring baseCmd
) {
    std::string result = executeRootCommand(UEFI_CMD);
    std::vector<int> foundTypes;

    size_t pos = 0;
    if (!result.empty()) {
        while ((pos = result.find(".img", pos)) != std::string::npos) {
            size_t start = result.rfind('/', pos);
            if (start == std::string::npos) start = 0;
            std::string filename = result.substr(start, pos - start);

            if (filename.find("-120hz") != std::string::npos) {
                foundTypes.push_back(120);
            } else if (filename.find("-90hz") != std::string::npos) {
                foundTypes.push_back(90);
            } else if (filename.find("-60hz") != std::string::npos) {
                foundTypes.push_back(60);
            }

            pos += 4;
        }

        if (foundTypes.empty()) {
            foundTypes.push_back(1);
        }
    }

    jintArray array = env->NewIntArray(foundTypes.size());
    env->SetIntArrayRegion(array, 0, foundTypes.size(), foundTypes.data());
    return array;
}

JNIEXPORT jint
JNICALL
Java_com_remtrik_m3khelper_util_VariablesKt_checkBootImages(
        JNIEnv *env,
        jobject jclass,
        jboolean noMount,
        jstring path
) {

    if (!noMount && access("sdcard/Windows/boot.img", F_OK) == 0) {
        return access("/sdcard/boot.img", F_OK) == 0 ? 3 /* BOTH */ : 2 /* WINDOWS */;
    }
    return access("/sdcard/boot.img", F_OK) == 0 ? 1 /* ANDROID */ : 0 /* NONE */;
}

}