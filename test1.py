import subprocess
import platform
import time
import os


def start_node_in_terminal(command, name):
    """
    Inicia um nó da aplicação em uma nova janela do terminal, sem fornecer input automático.

    :param command: Comando a ser executado no terminal.
    :param name: Nome ou ID do nó para fins de logging.
    """
    print(f"Iniciando {name} em uma nova janela de terminal...")

    # Identificar o sistema operacional
    system_name = platform.system()

    if system_name == "Windows":
        # No Windows: abrir o terminal CMD com o comando e deixá-lo aberto
        subprocess.Popen(
            ["start", "cmd", "/k", " ".join(command)], shell=True
        )
    elif system_name == "Linux":
        # No Linux: abrir o terminal (gnome-terminal ou bash) e manter aberto
        bash_command = " ".join(command)
        subprocess.Popen(
            ["gnome-terminal", "--", "bash", "-c", f"{bash_command}; exec bash"]
        )
    elif system_name == "Darwin":  # macOS
        script_dir = os.getcwd()  # Obter o diretório de trabalho atual no momento da execução
        bash_command = " ".join(command)
        bash_command_escaped = bash_command.replace('"', '\\"')  # Escapar aspas duplas
        subprocess.Popen([
            "osascript", "-e",
            f'tell application "Terminal" to do script "cd {script_dir}; {bash_command_escaped}"'
        ])
    else:
        raise Exception(
            "Sistema operacional não suportado. Apenas Windows, Linux e macOS são suportados."
        )

    # Aguardar antes de iniciar o próximo nó
    time.sleep(1)
    print(f"{name} foi iniciado com sucesso.")


# Get the correct classpath separator based on the operating system
classpath_separator = ";" if platform.system() == "Windows" else ":"
classpath1 = f"Server/target/classes{classpath_separator}Common/target/classes"
classpath2 =  f"Client/target/classes{classpath_separator}Common/target/classes"

# Configuração dos comandos para cada nó
nodes = [
    {
        "command": ["java", "-cp", classpath1, "Server.Main", "2"],
        "name": "Node-2",
    },
    {
        "command": ["java", "-cp", classpath1, "Server.Main", "1"],
        "name": "Node-1",
    },
    {
        "command": ["java", "-cp", classpath1, "Server.Main", "0", "yes"],
        "name": "Node-0",
    },
    {
        "command": ["java", "-cp", classpath1, "Server.Main", "3"],
        "name": "Node-3",
    },
    {
        "command": ["java", "-cp", classpath2, "Client.Main", "4"],
        "name": "Client1",
    },
]

print("NOTA: Certifique-se de que o projeto já foi compilado com 'mvn clean install'")

# Iniciar todos os nós nos terminais
for node_info in nodes:
    start_node_in_terminal(
        command=node_info["command"],
        name=node_info["name"],
    )

print("Todos os nós foram iniciados em terminais separados.")